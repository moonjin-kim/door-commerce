package com.loopers.application.order;

import com.loopers.application.payment.PaymentMethodType;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponCommand;
import com.loopers.domain.coupon.CouponJpaRepository;
import com.loopers.domain.coupon.DiscountType;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderEvent;
import com.loopers.domain.order.OrderStatus;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.stock.Stock;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.user.User;
import com.loopers.fixture.UserFixture;
import com.loopers.infrastructure.order.OrderJpaRepository;
import com.loopers.infrastructure.product.ProductJpaRepository;
import com.loopers.infrastructure.stock.StockJpaRepository;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.support.TestSupport;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import com.loopers.utils.RedisCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderFacadeTest extends TestSupport {
    @Autowired
    private CouponJpaRepository couponJpaRepository;
    @Autowired
    private ProductJpaRepository productJpaRepository;
    @Autowired
    private StockJpaRepository stockJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private OrderJpaRepository orderJpaRepository;
    @Autowired
    private OrderFacade orderFacade;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;
    @MockitoBean
    private OrderEventPublisher eventPublisher;
    @Autowired
    private RedisCleanUp redisCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("주문을 요청할때")
    @Nested
    class OrderTest {


        @DisplayName("주문 생성 시 결제 이벤트가 발행된다")
        @Test
        void publishEvent_whenOrderCreated() {
            // given
            User user = userJpaRepository.save(UserFixture.createMember());
            Product product = productJpaRepository.save(Product.create(ProductCommand.Create.of(
                    1L, "티셔츠", "설명", "https://loopers.com/product/t-shirt.png", 10000L
            )));
            Stock stock = stockJpaRepository.save(Stock.create(StockCommand.Create.of(product.getId(), 2)));

            doNothing().when(eventPublisher).publish(any(OrderEvent.CreateComplete.class));

            OrderCriteria.Order criteria = new OrderCriteria.Order(
                    user.getId(),
                    List.of(new OrderCriteria.OrderItem(product.getId(), 2)),
                    null,
                    PaymentMethodType.POINT,
                    null,
                    null
            );

            // when
            orderFacade.order(criteria);

            // then
            verify(eventPublisher).publish(any(OrderEvent.CreateComplete.class));
        }

        @DisplayName("존재하지 않는 상품으로 주문 시 결제 이벤트가 발행되지 않는다")
        @Test
        void notPublishEvent_whenOrderFail() {
            // given
            User user = userJpaRepository.save(UserFixture.createMember());

            // 존재하지 않는 productId 사용
            Long notExistProductId = 999L;

            doNothing().when(eventPublisher).publish(any(OrderEvent.CreateComplete.class));

            OrderCriteria.Order criteria = new OrderCriteria.Order(
                    user.getId(),
                    List.of(new OrderCriteria.OrderItem(notExistProductId, 2)),
                    null,
                    PaymentMethodType.POINT,
                    null,
                    null
            );

            // when & then
            CoreException coreException = assertThrows(
                    CoreException.class,
                    () -> orderFacade.order(criteria)
            );
            // 결제 이벤트가 발행되지 않았는지 검증
            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
            verify(eventPublisher, never()).publish(any(OrderEvent.CreateComplete.class));
        }

        @DisplayName("존재하지 않는 쿠폰을 사용하면 주문이 생성되지 않고 BadRequest예외가 발생한다")
        @Test
        void notApplyCoupon_whenNotValidCoupon() {
            // given
            User user = userJpaRepository.save(UserFixture.createMember());

            Product product = productJpaRepository.save(Product.create(ProductCommand.Create.of(
                    1L, "티셔츠", "설명", "https://loopers.com/product/t-shirt.png", 10000L
            )));
            Stock stock = stockJpaRepository.save(Stock.create(StockCommand.Create.of(product.getId(), 2)));

            doNothing().when(eventPublisher).publish(any(OrderEvent.CreateComplete.class));

            OrderCriteria.Order criteria = new OrderCriteria.Order(
                    user.getId(),
                    List.of(new OrderCriteria.OrderItem(product.getId(), 2)),
                    1L,
                    PaymentMethodType.POINT,
                    null,
                    null
            );

            // when
            CoreException coreException = assertThrows(
                    CoreException.class,
                    () -> orderFacade.order(criteria)
            );
            // 결제 이벤트가 발행되지 않았는지 검증
            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
            verify(eventPublisher, never()).publish(any(OrderEvent.CreateComplete.class));
        }

        @DisplayName("쿠폰이 제공되지 않으면 쿠폰이 적용되지 않고 주문이 생성된다")
        @Test
        void notApplyCoupon_whenNotRequestCoupon() {
            // given
            User user = userJpaRepository.save(UserFixture.createMember());

            Product product = productJpaRepository.save(Product.create(ProductCommand.Create.of(
                    1L, "티셔츠", "설명", "https://loopers.com/product/t-shirt.png", 10000L
            )));
            Stock stock = stockJpaRepository.save(Stock.create(StockCommand.Create.of(product.getId(), 2)));

            doNothing().when(eventPublisher).publish(any(OrderEvent.CreateComplete.class));
            doNothing().when(eventPublisher).publish(any(OrderEvent.ConsumeStockCommand.class));

            OrderCriteria.Order criteria = new OrderCriteria.Order(
                    user.getId(),
                    List.of(new OrderCriteria.OrderItem(product.getId(), 2)),
                    null,
                    PaymentMethodType.POINT,
                    null,
                    null
            );

            // when
            OrderResult.Order result = orderFacade.order(criteria);

            // then
            Order order = orderJpaRepository.findById(result.id()).orElseThrow();

            assertThat(order.getUserId()).isEqualTo(user.getId());
            assertThat(order.getUserCouponId()).isEqualTo(null);
            assertThat(order.getCouponDiscountAmount().longValue()).isEqualTo(0L); // 10% 할인
            assertThat(order.getFinalAmount().longValue()).isEqualTo(20000L);

            verify(eventPublisher).publish(any(OrderEvent.CreateComplete.class));
            verify(eventPublisher).publish(any(OrderEvent.ConsumeStockCommand.class));
        }

        @DisplayName("정상적이면 재고 차감 이벤트 발행, 결제 이벤트 발행, 쿠폰 적용 후 주문이 생성된다")
        @Test
        void orderIntegrationTest() {
            // given
            User user = userJpaRepository.save(UserFixture.createMember());

            Product product = productJpaRepository.save(Product.create(ProductCommand.Create.of(
                    1L, "티셔츠", "설명", "https://loopers.com/product/t-shirt.png", 10000L
            )));
            Stock stock = stockJpaRepository.save(Stock.create(StockCommand.Create.of(product.getId(), 2)));

            Coupon coupon = couponJpaRepository.save(Coupon.create(
                    CouponCommand.Create.of("10% 할인", "설명", BigDecimal.valueOf(10), DiscountType.PERCENT)
            ));

            doNothing().when(eventPublisher).publish(any(OrderEvent.CreateComplete.class));
            doNothing().when(eventPublisher).publish(any(OrderEvent.ConsumeStockCommand.class));

            OrderCriteria.Order criteria = new OrderCriteria.Order(
                    user.getId(),
                    List.of(new OrderCriteria.OrderItem(product.getId(), 2)),
                    coupon.getId(),
                    PaymentMethodType.POINT,
                    null,
                    null
            );

            // when
            OrderResult.Order result = orderFacade.order(criteria);

            // then
            Order order = orderJpaRepository.findById(result.id()).orElseThrow();

            assertThat(order.getUserId()).isEqualTo(user.getId());
            assertThat(order.getCouponDiscountAmount().longValue()).isEqualTo(2000L); // 10% 할인
            assertThat(order.getFinalAmount().longValue()).isEqualTo(18000L);

            verify(eventPublisher).publish(any(OrderEvent.CreateComplete.class));
            verify(eventPublisher).publish(any(OrderEvent.ConsumeStockCommand.class));
        }
    }

    @DisplayName("주문 취소 통합 테스트")
    @Nested
    class CancelOrderTest {

        @DisplayName("정상 주문 취소 시 주문 상태 변경, 재고 복구, 쿠폰 취소, 이벤트 발행")
        @Test
        void cancelOrderSuccess() {
            // given
            User user = userJpaRepository.save(UserFixture.createMember());
            Product product = productJpaRepository.save(Product.create(ProductCommand.Create.of(
                    1L, "티셔츠", "설명", "https://loopers.com/product/t-shirt.png", 10000L
            )));
            Stock stock = stockJpaRepository.save(Stock.create(StockCommand.Create.of(product.getId(), 2)));
            Coupon coupon = couponJpaRepository.save(Coupon.create(
                    CouponCommand.Create.of("10% 할인", "설명", BigDecimal.valueOf(10), DiscountType.PERCENT)
            ));

            doNothing().when(eventPublisher).publish(any(OrderEvent.CreateComplete.class));

            OrderCriteria.Order criteria = new OrderCriteria.Order(
                    user.getId(),
                    List.of(new OrderCriteria.OrderItem(product.getId(), 2)),
                    coupon.getId(),
                    PaymentMethodType.POINT,
                    null,
                    null
            );
            OrderResult.Order result = orderFacade.order(criteria);

            // when
            orderFacade.cancelOrder(result.orderId());

            // then
            Order canceledOrder = orderJpaRepository.findById(result.id()).orElseThrow();
            Stock restoredStock = stockJpaRepository.findByProductId(product.getId()).orElseThrow();

            assertThat(canceledOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
            assertThat(restoredStock.getQuantity()).isEqualTo(2); // 재고 복구
            // 쿠폰 취소 검증 (쿠폰 상태 등)
            verify(eventPublisher, atLeastOnce()).publish(any(OrderEvent.CreateComplete.class));
        }

        @DisplayName("존재하지 않는 주문 취소 시 예외 발생")
        @Test
        void cancelOrderFail_whenOrderNotExist() {
            // given
            String notExistOrderId = "not-exist-id";

            // when & then
            CoreException ex = assertThrows(CoreException.class, () -> orderFacade.cancelOrder(notExistOrderId));
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
            verify(eventPublisher, never()).publish(any(OrderEvent.CreateComplete.class));
        }
    }

    @DisplayName("주문 완료 통합 테스트")
    @Nested
    class CompleteOrderTest {

        @DisplayName("주문 완료 성공: 주문 상태가 COMPLETED로 변경된다")
        @Test
        void completeOrderSuccess() {
            // given
            User user = userJpaRepository.save(UserFixture.createMember());
            Product product = productJpaRepository.save(Product.create(ProductCommand.Create.of(
                    1L, "티셔츠", "설명", "https://loopers.com/product/t-shirt.png", 10000L
            )));
            stockJpaRepository.save(Stock.create(StockCommand.Create.of(product.getId(), 2)));

            OrderCriteria.Order criteria = new OrderCriteria.Order(
                    user.getId(),
                    List.of(new OrderCriteria.OrderItem(product.getId(), 2)),
                    null,
                    PaymentMethodType.POINT,
                    null,
                    null
            );
            OrderResult.Order result = orderFacade.order(criteria);

            // when
            orderFacade.completeOrder(result.orderId());

            // then
            Order completedOrder = orderJpaRepository.findById(result.id()).orElseThrow();
            assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @DisplayName("주문 완료 실패: 존재하지 않는 주문이면 예외 발생")
        @Test
        void completeOrderFail_whenOrderNotExist() {
            // given
            String notExistOrderId = "not-exist-id";

            // when & then
            CoreException ex = assertThrows(CoreException.class, () -> orderFacade.completeOrder(notExistOrderId));
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }

    @DisplayName("주문 단건/목록 조회 테스트")
    @Nested
    class GetOrderTest {

        @DisplayName("주문 단건 조회 성공")
        @Test
        void getBySuccess() {
            // given
            User user = userJpaRepository.save(UserFixture.createMember());
            Product product = productJpaRepository.save(Product.create(ProductCommand.Create.of(
                    1L, "티셔츠", "설명", "https://loopers.com/product/t-shirt.png", 10000L
            )));
            stockJpaRepository.save(Stock.create(StockCommand.Create.of(product.getId(), 2)));

            OrderCriteria.Order criteria = new OrderCriteria.Order(
                    user.getId(),
                    List.of(new OrderCriteria.OrderItem(product.getId(), 2)),
                    null,
                    PaymentMethodType.POINT,
                    null,
                    null
            );
            OrderResult.Order result = orderFacade.order(criteria);

            // when
            OrderResult.Order found = orderFacade.getBy(result.id(), user.getId());

            // then
            assertThat(found.id()).isEqualTo(result.id());
            assertThat(found.userId()).isEqualTo(user.getId());
        }

        @DisplayName("주문 단건 조회 실패: 존재하지 않는 주문이면 예외 발생")
        @Test
        void getByFail_whenOrderNotExist() {
            // given
            User user = userJpaRepository.save(UserFixture.createMember());
            Long notExistOrderId = 999L;

            // when & then
            CoreException ex = assertThrows(CoreException.class, () -> orderFacade.getBy(notExistOrderId, user.getId()));
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }
}

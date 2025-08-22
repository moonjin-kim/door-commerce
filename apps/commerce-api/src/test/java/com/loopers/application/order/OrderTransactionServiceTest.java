package com.loopers.application.order;

import com.loopers.application.order.coupon.CouponApplierCommand;
import com.loopers.application.order.payment.PaymentMethodType;
import com.loopers.domain.coupon.CouponJpaRepository;
import com.loopers.domain.order.Order;
import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointHistory;
import com.loopers.domain.point.PointStatus;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.stock.Stock;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.user.User;
import com.loopers.fixture.UserFixture;
import com.loopers.infrastructure.order.OrderJpaRepository;
import com.loopers.infrastructure.point.PointHistoryJpaRepository;
import com.loopers.infrastructure.point.PointJpaRepository;
import com.loopers.infrastructure.product.ProductJpaRepository;
import com.loopers.infrastructure.stock.StockJpaRepository;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.order.OrderV1Request;
import com.loopers.interfaces.api.order.OrderV1Response;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import com.loopers.utils.RedisCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()
class OrderTransactionServiceTest {
    @Autowired
    private CouponJpaRepository couponJpaRepository;
    @Autowired
    private ProductJpaRepository productJpaRepository;
    @Autowired
    private PointJpaRepository pointJpaRepository;
    @Autowired
    private PointHistoryJpaRepository pointHistoryJpaRepository;
    @Autowired
    private StockJpaRepository stockJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private OrderJpaRepository orderJpaRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;
    @Autowired
    private RedisCleanUp redisCleanUp;
    @Autowired
    private OrderTransactionService orderTransactionService;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
        redisCleanUp.truncateAll();
    }

    @DisplayName("주문을 생성할때")
    @Nested
    class OrderControllerTest {
        private static final String URL = "/api/v1/orders";

        @DisplayName("주문 생성 성공")
        @Test
        void returnOrder_whenCurrenCreate() {
            //given
            User user = userJpaRepository.save(
                    UserFixture.createMember()
            );

            Point point = Point.create(user.getId());
            point.charge(20000L);
            Point chargePoint = pointJpaRepository.save(
                    point
            );

            var product1 = productJpaRepository.save(
                    Product.create(ProductCommand.Create.of(
                            1L,
                            "루퍼스 공식 티셔츠",
                            "루퍼스의 공식 티셔츠입니다. 루퍼스는 루퍼스입니다.",
                            "https://loopers.com/product/t-shirt.png",
                            10000L
                    )));

            Stock stock = stockJpaRepository.save(Stock.create(
                    StockCommand.Create.of(product1.getId(), 2)
            ));
            var request = OrderCriteria.Order.of(
                    user.getId(),
                    List.of(new OrderCriteria.OrderItem(product1.getId(), 2)),
                    null,
                    PaymentMethodType.POINT,
                    null,
                    null
            );


            //when
            OrderResult.Order result = orderTransactionService.prepareOrder(request);

            //then
            Order order = orderJpaRepository.findAll().get(0);
            assertAll(
                    () -> assertNotNull(order),
                    () -> assertThat(order.getOrderId()).isNotNull(),
                    () -> assertThat(order.getUserId()).isEqualTo(user.getId()),
                    () -> assertThat(order.getTotalAmount().longValue()).isEqualTo(20000L)
            );
        }

        @DisplayName("주문이 생성되었을 때, 주문 수량만큼 재고가 차감된다.")
        @Test
        void decreaseStock_whenSuccessCreate() {
            //given
            User user = userJpaRepository.save(
                    UserFixture.createMember()
            );

            var product1 = productJpaRepository.save(
                    Product.create(ProductCommand.Create.of(
                            1L,
                            "루퍼스 공식 티셔츠",
                            "루퍼스의 공식 티셔츠입니다. 루퍼스는 루퍼스입니다.",
                            "https://loopers.com/product/t-shirt.png",
                            10000L
                    )));

            Stock stock = stockJpaRepository.save(Stock.create(
                    StockCommand.Create.of(product1.getId(), 2)
            ));

            var request = OrderCriteria.Order.of(
                    user.getId(),
                    List.of(new OrderCriteria.OrderItem(product1.getId(), 2)),
                    null,
                    PaymentMethodType.POINT,
                    null,
                    null
            );

            //when
            OrderResult.Order order = orderTransactionService.prepareOrder(request);

            //then
            Stock stock1 = stockJpaRepository.findByProductId(product1.getId()).get();
            assertThat(stock1.getQuantity()).isEqualTo(0);
        }

        @DisplayName("재고가 부족하면, 주문이 취소되고 400 Bad Request 응답을 반환한다.")
        @Test
        void returnBadRequest_when() {
            //given
            User user = userJpaRepository.save(
                    UserFixture.createMember()
            );

            var product1 = productJpaRepository.save(
                    Product.create(ProductCommand.Create.of(
                            1L,
                            "루퍼스 공식 티셔츠",
                            "루퍼스의 공식 티셔츠입니다. 루퍼스는 루퍼스입니다.",
                            "https://loopers.com/product/t-shirt.png",
                            10000L
                    )));

            Stock stock = stockJpaRepository.save(Stock.create(
                    StockCommand.Create.of(product1.getId(), 1)
            ));

            var request = OrderCriteria.Order.of(
                    user.getId(),
                    List.of(new OrderCriteria.OrderItem(product1.getId(), 2)),
                    null,
                    PaymentMethodType.POINT,
                    null,
                    null
            );


            //when
            CoreException result = assertThrows(CoreException.class, () -> {
                orderTransactionService.prepareOrder(request);
            });
            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.INSUFFICIENT_STOCK);
        }
    }
}

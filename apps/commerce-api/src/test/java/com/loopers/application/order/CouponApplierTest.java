package com.loopers.application.order;

import com.loopers.domain.coupon.*;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointCommand;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.stock.Stock;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.user.User;
import com.loopers.fixture.UserFixture;
import com.loopers.infrastructure.coupon.UserCouponJpaRepository;
import com.loopers.infrastructure.order.OrderJpaRepository;
import com.loopers.infrastructure.point.PointHistoryJpaRepository;
import com.loopers.infrastructure.point.PointJpaRepository;
import com.loopers.infrastructure.product.ProductJpaRepository;
import com.loopers.infrastructure.stock.StockJpaRepository;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CouponApplierTest {
    @Autowired
    private final CouponJpaRepository couponJpaRepository;
    @Autowired
    private final UserCouponJpaRepository userCouponJpaRepository;
    @Autowired
    private final OrderJpaRepository orderJpaRepository;
    @Autowired
    private final CouponApplier couponApplier;
    @Autowired
    private final DatabaseCleanUp databaseCleanUp;


    @Autowired
    public CouponApplierTest(
            CouponJpaRepository couponRepository,
            UserCouponJpaRepository userCouponRepository,
            OrderJpaRepository orderRepository,
            CouponApplier couponApplier,
            DatabaseCleanUp databaseCleanUp
    ) {
        this.couponJpaRepository = couponRepository;
        this.userCouponJpaRepository = userCouponRepository;
        this.orderJpaRepository = orderRepository;
        this.couponApplier = couponApplier;
        this.databaseCleanUp = databaseCleanUp;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("쿠폰을 적용할 때")
    @Nested
    class applyCouponTest {
        @DisplayName("정량 할인 쿠폰을 적용하면 주문에 쿠폰이 적용된다")
        @Test
        void applyCoupon() {
            //given
            Coupon coupon = couponJpaRepository.save(
                    Coupon.create(
                            CouponCommand.Create.of(
                                    "10% 할인 쿠폰",
                                    "10% 할인 쿠폰입니다.",
                                    BigDecimal.valueOf(10),
                                    DiscountType.PERCENT
                            )
                    )
            );

            OrderCommand.Order command = OrderCommand.Order.of(
                    1L,
                    List.of(
                            OrderCommand.OrderItem.of(1L, "상품1", 1000L, 1),
                            OrderCommand.OrderItem.of(2L, "상품2", 1000L, 5)
                    )
            );
            Order order = orderJpaRepository.save(Order.create(command));

            //when
            Order updatedOrder = couponApplier.applyCoupon(1L, coupon.getId(),order);

            //then
            assertAll(
                    ()-> assertThat(updatedOrder.getUserCouponId()).isNotNull(),
                    () -> assertThat(updatedOrder.getTotalAmount().longValue()).isEqualTo(6000L),
                    () -> assertThat(updatedOrder.getCouponDiscountAmount().longValue()).isEqualTo(600L),
                    () -> assertThat(updatedOrder.getFinalAmount().longValue()).isEqualTo(5400L)
            );
        }

        @DisplayName("정액 할인 쿠폰을 적용하면 주문에 쿠폰이 적용된다")
        @Test
        void applyCoupon_whenSendFixedCouponId() {
            //given
            Coupon coupon = couponJpaRepository.save(
                    Coupon.create(
                            CouponCommand.Create.of(
                                    "1000 할인 쿠폰",
                                    "1000원 할인 쿠폰입니다.",
                                    BigDecimal.valueOf(1000L),
                                    DiscountType.FIXED
                            )
                    )
            );

            OrderCommand.Order command = OrderCommand.Order.of(
                    1L,
                    List.of(
                            OrderCommand.OrderItem.of(1L, "상품1", 1000L, 1),
                            OrderCommand.OrderItem.of(2L, "상품2", 1000L, 5)
                    )
            );
            Order order = orderJpaRepository.save(Order.create(command));

            //when
            Order updatedOrder = couponApplier.applyCoupon(1L, coupon.getId(),order);

            //then
            assertAll(
                    ()-> assertThat(updatedOrder.getUserCouponId()).isNotNull(),
                    () -> assertThat(updatedOrder.getTotalAmount().longValue()).isEqualTo(6000L),
                    () -> assertThat(updatedOrder.getCouponDiscountAmount().longValue()).isEqualTo(1000L),
                    () -> assertThat(updatedOrder.getFinalAmount().longValue()).isEqualTo(5000L)
            );
        }

        @DisplayName("존재하지 않는 쿠폰 ID를 보내면 예외가 발생한다")
        @Test
        void throwNotFound_whenCouponIdNoExist() {
            //given
            OrderCommand.Order command = OrderCommand.Order.of(
                    1L,
                    List.of(
                            OrderCommand.OrderItem.of(1L, "상품1", 1000L, 1),
                            OrderCommand.OrderItem.of(2L, "상품2", 1000L, 5)
                    )
            );
            Order order = orderJpaRepository.save(Order.create(command));

            //when
            CoreException result = assertThrows(CoreException.class, () -> {
                couponApplier.applyCoupon(1L, 1L,order);
            });

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }

        @DisplayName("유저에게 발행되지 않은 쿠폰을 동시에 사용하여도 1개의 쿠폰만 발행 후 적용되고 나머지는 실패한다.")
        @Test
        void throwConcurrentModificationException_whenPointIsUsedSimultaneously() throws InterruptedException  {
            //given
            int threadCount = 10;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            Coupon coupon = couponJpaRepository.save(
                    Coupon.create(
                            CouponCommand.Create.of(
                                    "1000 할인 쿠폰",
                                    "1000원 할인 쿠폰입니다.",
                                    BigDecimal.valueOf(1000L),
                                    DiscountType.FIXED
                            )
                    )
            );

            OrderCommand.Order command = OrderCommand.Order.of(
                    1L,
                    List.of(
                            OrderCommand.OrderItem.of(1L, "상품1", 1000L, 1),
                            OrderCommand.OrderItem.of(2L, "상품2", 1000L, 5)
                    )
            );
            Order order = orderJpaRepository.save(Order.create(command));

            // 포인트 사용 실패 에러 저장 위치
            List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());

            //when
            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        couponApplier.applyCoupon(1L, 1L, order);
                    } catch (Exception e) {
                        exceptions.add(e);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            //then
            latch.await();
            assertThat(exceptions.size()).isEqualTo(threadCount - 1);
        }

        @DisplayName("쿠폰을 동시에 사용하여도 1번만 적용되고 나머지는 실패한다.")
        @Test
        void throwConcurrentModificationException_whenPointIsUsedSimultaneously2() throws InterruptedException  {
            //given
            int threadCount = 10;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            Coupon coupon = couponJpaRepository.save(
                    Coupon.create(
                            CouponCommand.Create.of(
                                    "1000 할인 쿠폰",
                                    "1000원 할인 쿠폰입니다.",
                                    BigDecimal.valueOf(1000L),
                                    DiscountType.FIXED
                            )
                    )
            );

            UserCoupon userCoupon = userCouponJpaRepository.save(UserCoupon.create(1L, coupon));

            OrderCommand.Order command = OrderCommand.Order.of(
                    1L,
                    List.of(
                            OrderCommand.OrderItem.of(1L, "상품1", 1000L, 1),
                            OrderCommand.OrderItem.of(2L, "상품2", 1000L, 5)
                    )
            );
            Order order = orderJpaRepository.save(Order.create(command));

            // 포인트 사용 실패 에러 저장 위치
            List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());

            //when
            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        couponApplier.applyCoupon(1L, 1L, order);
                    } catch (Exception e) {
                        exceptions.add(e);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            //then
            latch.await();
            UserCoupon updateUserCoupon = userCouponJpaRepository.findById(userCoupon.getId()).get();
            assertThat(exceptions.size()).isEqualTo(threadCount - 1);
            assertAll(
                    () -> assertThat(updateUserCoupon.getUsedAt()).isNotNull()
            );
        }
    }
}

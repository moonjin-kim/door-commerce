package com.loopers.application.order;

import com.loopers.application.order.coupon.CouponProcessor;
import com.loopers.application.order.coupon.CouponApplierCommand;
import com.loopers.application.order.coupon.CouponApplierInfo;
import com.loopers.domain.coupon.*;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderCommand;
import com.loopers.infrastructure.coupon.UserCouponJpaRepository;
import com.loopers.infrastructure.order.OrderJpaRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

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
    private final CouponProcessor couponApplier;
    @Autowired
    private final DatabaseCleanUp databaseCleanUp;


    @Autowired
    public CouponApplierTest(
            CouponJpaRepository couponRepository,
            UserCouponJpaRepository userCouponRepository,
            OrderJpaRepository orderRepository,
            CouponProcessor couponApplier,
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
            CouponApplierInfo.ApplyCoupon updatedOrder = couponApplier.applyCoupon(
                    CouponApplierCommand.Apply.of(
                            1L,
                            coupon.getId(),
                            order.getId(),
                            order.getTotalAmount().value()
                    )
            );

            //then
            assertAll(
                    ()-> assertThat(updatedOrder.userCouponId()).isNotNull(),
                    () -> assertThat(updatedOrder.discountAmount().longValue()).isEqualTo(600L)
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
            CouponApplierInfo.ApplyCoupon updatedOrder = couponApplier.applyCoupon(
                    CouponApplierCommand.Apply.of(
                            1L,
                            coupon.getId(),
                            order.getId(),
                            order.getTotalAmount().value()
                    )
            );

            //then
            assertAll(
                    ()-> assertThat(updatedOrder.userCouponId()).isNotNull(),
                    () -> assertThat(updatedOrder.discountAmount().longValue()).isEqualTo(1000L)
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
                couponApplier.applyCoupon(
                        CouponApplierCommand.Apply.of(
                                1L,
                                1L, // 존재하지 않는 쿠폰 ID
                                order.getId(),
                                order.getTotalAmount().value()
                        )
                );
            });

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }
}

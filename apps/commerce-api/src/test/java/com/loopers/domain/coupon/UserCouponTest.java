package com.loopers.domain.coupon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserCouponTest {

    @DisplayName("유저 쿠폰을 생성할 때")
    @Nested
    class CreateUserCoupon {
        @DisplayName("유효한 유저 ID와 쿠폰이 주어지면 성공적으로 생성된다")
        @Test
        void createUserCoupon() {
            // Given
            String name = "10% 할인 쿠폰";
            String description = "10% 할인 쿠폰입니다.";
            BigDecimal value = new BigDecimal(100);
            DiscountType type = DiscountType.FIXED;
            Coupon coupon = Coupon.create(new CouponCommand.Create(name, description, value, type));

            // when
            UserCoupon userCoupon = UserCoupon.create(1L, coupon);

            // Then
            assertAll(
                    () -> assertThat(userCoupon.getId()).isNotNull(),
                    () -> assertThat(userCoupon.getUserId()).isEqualTo(1L),
                    () -> assertThat(userCoupon.getCouponId()).isEqualTo(coupon.getId()),
                    () -> assertThat(userCoupon.getIssuedAt()).isNotNull(),
                    () -> assertThat(userCoupon.getUsedAt()).isNull()
            );
        }

        @DisplayName("유효한 유저 ID와 쿠폰이 주어지면 성공적으로 생성된다")
        @Test
        void throwIllegalArgumentException_whenNullUserId() {
            // Given
            String name = "10% 할인 쿠폰";
            String description = "10% 할인 쿠폰입니다.";
            BigDecimal value = new BigDecimal(100);
            DiscountType type = DiscountType.FIXED;
            Coupon coupon = Coupon.create(new CouponCommand.Create(name, description, value, type));

            // when
            // Then
            assertThrows(IllegalArgumentException.class, () -> {
                UserCoupon.create(null, coupon);
            });
        }

        @DisplayName("유효한 유저 ID와 쿠폰이 주어지면 성공적으로 생성된다")
        @Test
        void throwIllegalArgumentException_whenNullCoupon() {
            // Given
            String name = "10% 할인 쿠폰";
            String description = "10% 할인 쿠폰입니다.";
            BigDecimal value = new BigDecimal(100);
            DiscountType type = DiscountType.FIXED;
            Coupon coupon = Coupon.create(new CouponCommand.Create(name, description, value, type));

            // when

            // Then
            assertThrows(IllegalArgumentException.class, () -> {
                UserCoupon.create(null, coupon);
            });
        }
    }

    @DisplayName("쿠폰을 사용할 때")
    @Nested
    class Use {
        @DisplayName("유효한 주문ID / 사용 일시가 주어지면 쿠폰을 사용한다")
        @Test
        void createUserCoupon() {
            // Given
            String name = "10% 할인 쿠폰";
            String description = "10% 할인 쿠폰입니다.";
            BigDecimal value = new BigDecimal(100);
            DiscountType type = DiscountType.FIXED;
            Coupon coupon = Coupon.create(new CouponCommand.Create(name, description, value, type));
            UserCoupon userCoupon = UserCoupon.create(1L, coupon);
            Long orderId = 1L;
            var now = LocalDateTime.now();

            // when
            userCoupon.use(orderId, now);

            // Then
            assertAll(
                    () -> assertThat(userCoupon.getId()).isNotNull(),
                    () -> assertThat(userCoupon.getUserId()).isEqualTo(1L),
                    () -> assertThat(userCoupon.getCouponId()).isEqualTo(coupon.getId()),
                    () -> assertThat(userCoupon.getIssuedAt()).isNotNull(),
                    () -> assertThat(userCoupon.getUsedAt()).isEqualTo(now)
            );
        }

        @DisplayName("유효한 주문ID / 사용 일시가 주어지면 쿠폰을 사용하면 쿠폰 사용 기록이 생성된다")
        @Test
        void createCouponHistory_whenCreateUserCoupon() {
            // Given
            String name = "10% 할인 쿠폰";
            String description = "10% 할인 쿠폰입니다.";
            BigDecimal value = new BigDecimal(100);
            DiscountType type = DiscountType.FIXED;
            Coupon coupon = Coupon.create(new CouponCommand.Create(name, description, value, type));
            UserCoupon userCoupon = UserCoupon.create(1L, coupon);
            Long orderId = 1L;
            var now = LocalDateTime.now();

            // when
            userCoupon.use(orderId, now);

            // Then
            assertAll(
                    () -> assertThat(userCoupon.getId()).isNotNull(),
                    () -> assertThat(userCoupon.getUserId()).isEqualTo(1L),
                    () -> assertThat(userCoupon.getCouponId()).isEqualTo(coupon.getId()),
                    () -> assertThat(userCoupon.getIssuedAt()).isNotNull(),
                    () -> assertThat(userCoupon.getUsedAt()).isEqualTo(now),
                    () -> assertThat(userCoupon.getUserCouponHistories()).hasSize(1),
                    () -> assertThat(userCoupon.getUserCouponHistories().get(0).getOrderId()).isEqualTo(orderId),
                    () -> assertThat(userCoupon.getUserCouponHistories().get(0).getUsedType()).isEqualTo(CouponHistoryType.USED)
            );
        }

        @DisplayName("주문 ID가 null이면 IllegalArgumentException을 던진다")
        @Test
        void throwIllegalArgumentException_whenNullOrderId() {
            // Given
            String name = "10% 할인 쿠폰";
            String description = "10% 할인 쿠폰입니다.";
            BigDecimal value = new BigDecimal(100);
            DiscountType type = DiscountType.FIXED;
            Coupon coupon = Coupon.create(new CouponCommand.Create(name, description, value, type));
            UserCoupon userCoupon = UserCoupon.create(1L, coupon);
            Long orderId = null;
            var now = LocalDateTime.now();

            // when

            // Then
            assertThrows(IllegalArgumentException.class, () -> {
                userCoupon.use(orderId, now);
            });
        }

        @DisplayName("사용 날짜가 null이면 IllegalArgumentException을 던진다")
        @Test
        void throwIllegalArgumentException_whenNullUsedAt() {
            // Given
            String name = "10% 할인 쿠폰";
            String description = "10% 할인 쿠폰입니다.";
            BigDecimal value = new BigDecimal(100);
            DiscountType type = DiscountType.FIXED;
            Coupon coupon = Coupon.create(new CouponCommand.Create(name, description, value, type));
            UserCoupon userCoupon = UserCoupon.create(1L, coupon);
            Long orderId = null;
            var now = LocalDateTime.now();

            // when

            // Then
            assertThrows(IllegalArgumentException.class, () -> {
                userCoupon.use(orderId, now);
            });
        }

        @DisplayName("이미 사용한 쿠폰을 다시 사용하려고 하면 IllegalStateException을 던진다")
        @Test
        void throwIllegalStateException_whenAlreadyUsed() {
            // Given
            String name = "10% 할인 쿠폰";
            String description = "10% 할인 쿠폰입니다.";
            BigDecimal value = new BigDecimal(100);
            DiscountType type = DiscountType.FIXED;
            Coupon coupon = Coupon.create(new CouponCommand.Create(name, description, value, type));
            UserCoupon userCoupon = UserCoupon.create(1L, coupon);
            Long orderId = 1L;
            var now = LocalDateTime.now();
            userCoupon.use(orderId, now);

            // when

            // Then
            assertThrows(IllegalStateException.class, () -> {
                userCoupon.use(orderId, now);
            });
        }
    }

}

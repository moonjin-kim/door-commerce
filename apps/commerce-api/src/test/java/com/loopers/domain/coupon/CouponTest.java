package com.loopers.domain.coupon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CouponTest {

    @DisplayName("쿠폰을 생성할 때")
    @Nested
    class Create {
        @DisplayName("유효한 쿠폰 정보로 생성하면 성공한다")
        @Test
        void validCouponCreation() {
            // given
            String name = "10% 할인 쿠폰";
            String description = "10% 할인 쿠폰입니다.";
            BigDecimal value = new BigDecimal("10");
            DiscountType type = DiscountType.PERCENT;

            // when
            Coupon coupon = Coupon.create(new CouponCommand.Create(name, description, value, type));

            // then
            assertAll(
                    ()-> assertThat(coupon).isNotNull(),
                    () -> assertThat(coupon.getName()).isEqualTo(name),
                    () -> assertThat(coupon.getDescription()).isEqualTo(description)
            );
        }

        @DisplayName("쿠폰 이름이 비워있져있으면 IllegalArgumentException을 던진다")
        @Test
        void unValidCouponName_throwIllegalArgumentException() {
            // given
            String name = null;
            String description = "10% 할인 쿠폰입니다.";
            BigDecimal value = new BigDecimal("10");
            DiscountType type = DiscountType.PERCENT;

            // when

            // then
            assertThrows(IllegalArgumentException.class, () -> {
                Coupon.create(new CouponCommand.Create(name, description, value, type));
            });
        }

        @DisplayName("쿠폰 설명이 비워있져있으면 IllegalArgumentException을 던진다")
        @Test
        void unValidCouponDescription_throwIllegalArgumentException() {
            // given
            String name = "10% 할인 쿠폰";
            String description = null;
            BigDecimal value = new BigDecimal("10");
            DiscountType type = DiscountType.PERCENT;

            // when

            // then
            assertThrows(IllegalArgumentException.class, () -> {
                Coupon.create(new CouponCommand.Create(name, description, value, type));
            });
        }

        @DisplayName("쿠폰 타입을 Percent로 설정하고, 유효한 percent 받으면 Percent 쿠폰이 생성된다")
        @ParameterizedTest
        @ValueSource(strings = {
                "1",
                "100"
        })
        void validPercent_throwIllegalArgumentException(String value) {
            // given
            String name = "10% 할인 쿠폰";
            String description = "10% 할인 쿠폰입니다.";
            BigDecimal percent = new BigDecimal(value);
            DiscountType type = DiscountType.PERCENT;

            // when
            Coupon coupon = Coupon.create(new CouponCommand.Create(name, description, percent, type));

            // then
            assertAll(
                    () -> assertThat(coupon).isNotNull(),
                    () -> assertThat(coupon.getName()).isEqualTo(name),
                    () -> assertThat(coupon.getDescription()).isEqualTo(description),
                    () -> assertThat(coupon.getDiscountValue()).isEqualTo(percent),
                    () -> assertThat(coupon.getDiscountType()).isEqualTo(type)
            );
        }

        @DisplayName("쿠폰 타입을 Percent로 설정하고, 유효하지 percent 받으면 IllegalArgumentException을 던진다")
        @ParameterizedTest
        @ValueSource(strings = {
                "0",
                "101"
        })
        void unvalidPercent_throwIllegalArgumentException(String value) {
            // given
            String name = "10% 할인 쿠폰";
            String description = "10% 할인 쿠폰입니다.";
            BigDecimal percent = new BigDecimal(value);
            DiscountType type = DiscountType.PERCENT;

            // when

            // then
            assertThrows(IllegalArgumentException.class, () -> {
                Coupon.create(new CouponCommand.Create(name, description, percent, type));
            });
        }

        @DisplayName("쿠폰 타입을 Fixed로 설정하고, 유효한 value 받으면 Percent 쿠폰이 생성된다")
        @ParameterizedTest
        @ValueSource(strings = {
                "1001",
                "100"
        })
        void validFixed_throwIllegalArgumentException(String number) {
            // given
            String name = "10% 할인 쿠폰";
            String description = "10% 할인 쿠폰입니다.";
            BigDecimal value = new BigDecimal(number);
            DiscountType type = DiscountType.FIXED;

            // when
            Coupon coupon = Coupon.create(new CouponCommand.Create(name, description, value, type));

            // then
            assertAll(
                    () -> assertThat(coupon).isNotNull(),
                    () -> assertThat(coupon.getName()).isEqualTo(name),
                    () -> assertThat(coupon.getDescription()).isEqualTo(description),
                    () -> assertThat(coupon.getDiscountValue()).isEqualTo(value),
                    () -> assertThat(coupon.getDiscountType()).isEqualTo(type)
            );
        }

        @DisplayName("쿠폰 타입을 FIXED로 설정하고, value가 올바르지 않으면 IllegalArgumentException을 던진다")
        @ParameterizedTest
        @ValueSource(strings = {
                "0",
                "99"
        })
        void unvalidFIXAndUnValidValue_throwIllegalArgumentException(String value) {
            // given
            String name = "10% 할인 쿠폰";
            String description = "10% 할인 쿠폰입니다.";
            BigDecimal percent = new BigDecimal(value);
            DiscountType type = DiscountType.FIXED;

            // when

            // then
            assertThrows(IllegalArgumentException.class, () -> {
                Coupon.create(new CouponCommand.Create(name, description, percent, type));
            });
        }
    }
}

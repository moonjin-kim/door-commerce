package com.loopers.domain.coupon.policy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FixedAmountDiscountPolicyTest {

    @DisplayName("액 할인 정책을 생성할때")
    @Nested
    class FixAmountDiscountPolicyConstructorTest {

        @DisplayName("할인금액이 100 미만인 경우 IllegalArgumentException 예외 발생한다")
        @Test
        void testInvalidRateZeroOrNegative() {
            assertThrows(IllegalArgumentException.class, () -> new FixedAmountDiscountPolicy(new BigDecimal(99)));
            assertThrows(IllegalArgumentException.class, () -> new FixedAmountDiscountPolicy(new BigDecimal(-1)));
        }


        @DisplayName("유효한 할인금액으로 생성 성공한다.")
        @Test
        void testValidRate() {
            FixedAmountDiscountPolicy policy = new FixedAmountDiscountPolicy(new BigDecimal(100));
            assertNotNull(policy);
        }
    }

    @DisplayName("정책에서 할인 금액을 계산할 때")
    @Nested
    class calculateDiscount{

        @DisplayName("유효한 가격에 대해 할인 금액을 계산한다")
        @Test
        void testCalculateDiscount() {
            //given
            FixedAmountDiscountPolicy policy = new FixedAmountDiscountPolicy(new BigDecimal(1000));

            //when
            BigDecimal discount = policy.calculateDiscount(BigDecimal.valueOf(1000L));

            //then
            assertThat(discount).isEqualTo(new BigDecimal(1000));
        }

        @DisplayName("할인이 필요한 원본 가격이 할인 금액보다 작거은 경우 IllegalArgumentException 예외가 발생한다")
        @Test
        void throwIllegalArgumentException_InvalidPrice() {
            FixedAmountDiscountPolicy policy = new FixedAmountDiscountPolicy(new BigDecimal(1000));

            assertThrows(IllegalArgumentException.class, () -> policy.calculateDiscount(new BigDecimal(999)));
        }
    }
}

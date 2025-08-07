package com.loopers.domain.coupon.policy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PercentDiscountPolicyTest {

    @DisplayName("정률 할인 정책을 생성할때")
    @Nested
    class PercentDiscountPolicyConstructorTest {

        @DisplayName("할인율이 0 이하인 경우 IllegalArgumentException 예외 발생한다")
        @Test
        void testInvalidRateZeroOrNegative() {
            assertThrows(IllegalArgumentException.class, () -> new PercentDiscountPolicy(BigDecimal.ZERO));
            assertThrows(IllegalArgumentException.class, () -> new PercentDiscountPolicy(new BigDecimal(-1)));
        }

        @DisplayName("할인율이 100 초과인 경우 IllegalArgumentException 예외 발생한다")
        @Test
        void testInvalidRateOverHundred() {
            assertThrows(IllegalArgumentException.class, () -> new PercentDiscountPolicy(new BigDecimal(101)));
        }

        @DisplayName("유효한 할인율로 생성 성공한다.")
        @Test
        void testValidRate() {
            PercentDiscountPolicy policy = new PercentDiscountPolicy(new BigDecimal(10));
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
            PercentDiscountPolicy policy = new PercentDiscountPolicy(new BigDecimal(10));

            //when
            BigDecimal discount = policy.calculateDiscount(BigDecimal.valueOf(1000L));

            //then
            assertThat(discount).isEqualTo(new BigDecimal(100));
        }

        @DisplayName("가격이 0 이하인 경우 IllegalArgumentException 예외가 발생한다")
        @Test
        void throwIllegalArgumentException_InvalidPrice() {
            PercentDiscountPolicy policy = new PercentDiscountPolicy(new BigDecimal(10));

            assertThrows(IllegalArgumentException.class, () -> policy.calculateDiscount(BigDecimal.ZERO));
            assertThrows(IllegalArgumentException.class, () -> policy.calculateDiscount(new BigDecimal(-1)));
        }
    }
}

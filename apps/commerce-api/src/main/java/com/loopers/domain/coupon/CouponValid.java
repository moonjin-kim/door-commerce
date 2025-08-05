package com.loopers.domain.coupon;

import com.loopers.domain.coupon.policy.DiscountPolicy;
import com.loopers.domain.coupon.policy.FixedAmountDiscountPolicy;
import com.loopers.domain.coupon.policy.PercentDiscountPolicy;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import java.math.BigDecimal;

public class CouponValid {
    static void validateName(String name) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("쿠폰명은 비어있을 수 없습니다.");
        }
    }

    static void validateDescription(String description) {
        if(description == null || description.isEmpty()) {
            throw new IllegalArgumentException("쿠폰 설명은 비어있을 수 없습니다.");
        }
    }

    static void validateDiscountPolicy(BigDecimal value, DiscountType discountType) {
        DiscountPolicy policy = switch (discountType) {
            case PERCENT -> new PercentDiscountPolicy(value);
            case FIXED -> new FixedAmountDiscountPolicy(value);
            default -> throw new IllegalArgumentException("잘못된 타입의 쿠폰입니다. 타입: " + discountType);
        };
    }
}

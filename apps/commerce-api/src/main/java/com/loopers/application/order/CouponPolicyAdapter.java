package com.loopers.application.order;

import com.loopers.domain.coupon.UserCoupon;
import com.loopers.domain.coupon.policy.DiscountPolicy;
import com.loopers.domain.coupon.policy.FixedAmountDiscountPolicy;
import com.loopers.domain.coupon.policy.PercentDiscountPolicy;
import org.springframework.stereotype.Component;

@Component
public class CouponPolicyAdapter {
    public DiscountPolicy getPolicy(UserCoupon userCoupon) {
        return switch (userCoupon.getType()) {
            case PERCENT -> new PercentDiscountPolicy(userCoupon.getValue());
            case FIXED -> new FixedAmountDiscountPolicy(userCoupon.getValue());
            default -> throw new IllegalArgumentException("잘못된 타입의 쿠폰입니다. 타입: " + userCoupon.getType());
        };
    }
}

package com.loopers.application.order.coupon;

import java.math.BigDecimal;

public class CouponApplierInfo {
    public record ApplyCoupon(
            Long userCouponId,
            BigDecimal discountAmount
    ) {
        static ApplyCoupon of(Long couponId, BigDecimal discountAmount) {
            if (couponId == null || discountAmount == null || discountAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Coupon ID and discount amount must be valid");
            }
            return new ApplyCoupon(couponId, discountAmount);
        }
    }
}

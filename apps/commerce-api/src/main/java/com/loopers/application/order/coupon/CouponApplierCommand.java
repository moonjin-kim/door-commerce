package com.loopers.application.order.coupon;

import java.math.BigDecimal;

public class CouponApplierCommand {
    public record ApplyCoupon(
            Long userId,
            Long couponId,
            Long orderId,
            BigDecimal totalAmount
    ) {
        public ApplyCoupon {
            if (userId == null || couponId == null || orderId == null || totalAmount == null) {
                throw new IllegalArgumentException("User ID, Coupon ID, and Order cannot be null");
            }
        }

        public static ApplyCoupon of(Long userId, Long couponId, Long orderId, BigDecimal totalAmount) {
            return new ApplyCoupon(userId, couponId, orderId, totalAmount);
        }
    }
}

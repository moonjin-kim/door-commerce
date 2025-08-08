package com.loopers.domain.coupon;

import java.math.BigDecimal;

public class CouponCommand {
    public record Create(
        String name,
        String description,
        BigDecimal value,
        DiscountType type
    ) {
        public static Create of(String name, String description, BigDecimal value, DiscountType type) {
            return new Create(name, description, value, type);
        }
    }

    public record Get(
        Long userId,
        Long couponId
    ) {
        public static Get of(Long userId, Long couponId) {
            return new Get(userId, couponId);
        }
    }
}

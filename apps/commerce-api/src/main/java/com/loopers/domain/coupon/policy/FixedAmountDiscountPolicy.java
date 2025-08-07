package com.loopers.domain.coupon.policy;

import java.math.BigDecimal;

public class FixedAmountDiscountPolicy implements DiscountPolicy {
    private final BigDecimal amount; // 할인 금액

    public FixedAmountDiscountPolicy(BigDecimal amount) {
        if (amount.compareTo(new BigDecimal(100)) < 0) {
            throw new IllegalArgumentException("할인 금액은 100 이상이어야 합니다.");
        }

        this.amount = amount;
    }

    @Override
    public BigDecimal calculateDiscount(BigDecimal price) {
        if(price.compareTo(amount) < 0) {
            throw new IllegalArgumentException("주문 금액은 할인 가격보다 커야합니다.");
        }

        return amount;
    }
}

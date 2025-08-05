package com.loopers.domain.coupon.policy;

import java.math.BigDecimal;

public class PercentDiscountPolicy implements DiscountPolicy {
    private final BigDecimal rate; // 할인율 (e.g., 10 for 10%)

    public PercentDiscountPolicy(BigDecimal rate) {
        if(rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("할인 비율은 0 초과이어야 합니다.");
        }

        if(rate.compareTo(new BigDecimal(100)) > 0) {
            throw new IllegalArgumentException("할인 비율은 100 이하이어야 합니다.");
        }

        this.rate = rate;
    }

    @Override
    public BigDecimal applyDiscount(BigDecimal price) {
        if(price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("가격은 0 초과이어야 합니다.");
        }

        return price.multiply(rate).divide(new BigDecimal(100));
    }
}

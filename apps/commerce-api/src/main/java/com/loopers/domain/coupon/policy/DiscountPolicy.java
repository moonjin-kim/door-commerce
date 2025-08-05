package com.loopers.domain.coupon.policy;

import java.math.BigDecimal;

public interface DiscountPolicy {
    BigDecimal applyDiscount(BigDecimal price);
}

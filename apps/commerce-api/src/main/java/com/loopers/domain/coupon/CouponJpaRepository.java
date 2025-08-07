package com.loopers.domain.coupon;

import java.util.Optional;

public interface CouponJpaRepository {
    Coupon save(Coupon coupon);
    Optional<Coupon> findBy(Long id);
}

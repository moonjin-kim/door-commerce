package com.loopers.domain.coupon;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserCouponHistory {

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CouponHistoryType usedType;

    protected UserCouponHistory(Long orderId, CouponHistoryType usedType) {
        this.orderId = orderId;
        this.usedType = usedType;
    }

    public static UserCouponHistory create(Long orderId, CouponHistoryType usedType) {
        return new UserCouponHistory(orderId, usedType);
    }
}

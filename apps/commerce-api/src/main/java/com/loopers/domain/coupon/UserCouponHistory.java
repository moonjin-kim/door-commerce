package com.loopers.domain.coupon;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "user_coupon_history")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCouponHistory extends BaseEntity {
    @Column(nullable = false)
    private Long orderId;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CouponHistoryType usedType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_coupon_id", nullable = false)
    private UserCoupon userCoupon;

    protected UserCouponHistory(Long orderId, CouponHistoryType usedType) {
        this.orderId = orderId;
        this.usedType = usedType;
    }

    public static UserCouponHistory create(Long orderId, CouponHistoryType usedType) {
        return new UserCouponHistory(orderId, usedType);
    }
}

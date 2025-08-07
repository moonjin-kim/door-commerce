package com.loopers.domain.coupon;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.Money;
import com.loopers.domain.coupon.policy.DiscountPolicy;
import com.loopers.domain.coupon.policy.FixedAmountDiscountPolicy;
import com.loopers.domain.coupon.policy.PercentDiscountPolicy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "user_coupon")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class UserCoupon extends BaseEntity {
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @Column(nullable = true)
    private LocalDateTime usedAt;

    @Column(nullable = false)
    private Long couponId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType type;

    @Column(nullable = false)
    private BigDecimal value;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "user_coupon_history", // OrderItem 정보를 저장할 별도 테이블의 이름
            joinColumns = @JoinColumn(name = "user_coupon_id") // 이 테이블에서 Order를 참조할 외래 키(FK)
    )
    private List<UserCouponHistory> userCouponHistories = new ArrayList<>();

    @Version()
    private Long Version;

    protected UserCoupon(
            Long userId,
            Long couponId,
            DiscountType type,
            BigDecimal value,
            LocalDateTime issuedAt,
            LocalDateTime usedAt
    ) {
        if (userId == null) {
            throw new IllegalArgumentException("유저 ID는 null일 수 없습니다.");
        }
        if (couponId == null) {
            throw new IllegalArgumentException("쿠폰은 null일 수 없습니다.");
        }
        if (issuedAt == null) {
            throw new IllegalArgumentException("발급 일시는 null일 수 없습니다.");
        }

        this.userId = userId;
        this.couponId = couponId;
        this.issuedAt = issuedAt;
        this.type = type;
        this.value = value;
        this.usedAt = usedAt;
        this.userCouponHistories = new ArrayList<>();
    }

    public static UserCoupon create(Long userId, Coupon coupon) {
        return new UserCoupon(
                userId,
                coupon.getId(),
                coupon.getType(),
                coupon.getValue(),
                LocalDateTime.now(),
                null
        );
    }

    public void use(Long orderId, LocalDateTime usedAt) {
        if (orderId == null) {
            throw new IllegalArgumentException("주문 ID는 null일 수 없습니다.");
        }
        if (usedAt == null) {
            throw new IllegalArgumentException("사용 일시는 null일 수 없습니다.");
        }
        if(this.usedAt != null) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }

        this.usedAt = usedAt;

        userCouponHistories.add(UserCouponHistory.create(orderId, CouponHistoryType.USED));
    }

    public void cancel(Long orderId) {
        if (usedAt == null) {
            throw new IllegalStateException("사용되지 않은 쿠폰은 취소할 수 없습니다.");
        }

        this.usedAt = null;

        userCouponHistories.add(UserCouponHistory.create(orderId, CouponHistoryType.CANCELLED));
    }

    public Money calculateDiscount(BigDecimal originalAmount) {
        DiscountPolicy policy = switch (type) {
            case PERCENT -> new PercentDiscountPolicy(value);
            case FIXED -> new FixedAmountDiscountPolicy(value);
            default -> throw new IllegalArgumentException("잘못된 타입의 쿠폰입니다. 타입: " + type);
        };

        BigDecimal discountAmount = policy.calculateDiscount(originalAmount);
        return new Money(discountAmount);
    }

    public DiscountPolicy getDiscountPolicy() {
        return switch (type) {
            case PERCENT -> new PercentDiscountPolicy(value);
            case FIXED -> new FixedAmountDiscountPolicy(value);
            default -> throw new IllegalArgumentException("잘못된 타입의 쿠폰입니다. 타입: " + type);
        };
    }

    public boolean isUsed() {
        return this.usedAt != null;
    }

    public void validate() {
        if (this.usedAt != null) {
            throw new IllegalArgumentException("이미 사용된 쿠폰입니다.");
        }
    }
}

package com.loopers.domain.coupon;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

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
            Coupon coupon,
            LocalDateTime issuedAt,
            LocalDateTime usedAt
    ) {
        if (userId == null) {
            throw new IllegalArgumentException("유저 ID는 null일 수 없습니다.");
        }
        if (coupon == null) {
            throw new IllegalArgumentException("쿠폰은 null일 수 없습니다.");
        }
        if (issuedAt == null) {
            throw new IllegalArgumentException("발급 일시는 null일 수 없습니다.");
        }

        this.userId = userId;
        this.coupon = coupon;
        this.issuedAt = issuedAt;
        this.usedAt = usedAt;
        this.userCouponHistories = new ArrayList<>();
    }

    public static UserCoupon create(Long userId, Coupon coupon) {
        return new UserCoupon(userId, coupon, LocalDateTime.now(), null);
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

    public boolean isUsed() {
        return this.usedAt != null;
    }
}

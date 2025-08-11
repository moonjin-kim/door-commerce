package com.loopers.application.order;

import com.loopers.domain.coupon.CouponCommand;
import com.loopers.domain.coupon.CouponService;
import com.loopers.domain.coupon.UserCoupon;
import com.loopers.domain.coupon.policy.DiscountPolicy;
import com.loopers.domain.order.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CouponApplier {
    private final CouponService couponService;
    private final CouponPolicyAdapter couponPolicyAdapter;

    @Transactional
    public Order applyCoupon(Long userId, Long couponId, Order order) {
        UserCoupon userCoupon = couponService.getUserCoupon(
                CouponCommand.Get.of(userId, couponId)
        );
        DiscountPolicy discountPolicy = couponPolicyAdapter.getPolicy(userCoupon);
        order.applyCoupon(userCoupon.getId(), discountPolicy);

        // 쿠폰 사용 기록 추가
        userCoupon.use(order.getId(), LocalDateTime.now());
        couponService.saveUserCoupon(userCoupon);

        return order;
    }
}

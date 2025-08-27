package com.loopers.application.order.coupon;

import com.loopers.domain.coupon.CouponCommand;
import com.loopers.domain.coupon.CouponService;
import com.loopers.domain.coupon.UserCoupon;
import com.loopers.domain.coupon.policy.DiscountPolicy;
import com.loopers.domain.order.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CouponProcessor {
    private final CouponService couponService;
    private final CouponPolicyProcessor couponPolicyProcessor;

    @Transactional
    public CouponApplierInfo.ApplyCoupon applyCoupon(CouponApplierCommand.Apply command) {
        UserCoupon userCoupon = couponService.getUserCoupon(
                CouponCommand.Get.of(command.userId(), command.couponId())
        );
        DiscountPolicy discountPolicy = couponPolicyProcessor.getPolicy(userCoupon);
        BigDecimal discountAtAmount = discountPolicy.calculateDiscount(command.totalAmount());

        // 쿠폰 사용 기록 추가
        userCoupon.use(command.orderId(), LocalDateTime.now());
        couponService.saveUserCoupon(userCoupon);

        return CouponApplierInfo.ApplyCoupon.of(userCoupon.getId(), discountAtAmount);
    }

    @Transactional
    public void cancelCoupon(Order order) {
        couponService.cancel(order.getUserId(), order.getUserCouponId(), order.getId());
    }
}

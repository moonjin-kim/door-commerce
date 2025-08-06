package com.loopers.domain.coupon;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final UserCouponRepository userCouponRepository;
    private final CouponRepository CouponRepository;

    @Transactional
    public UserCoupon getUserCoupon(CouponCommand.Get command) {
        Coupon coupon = CouponRepository.findBy(command.couponId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 쿠폰입니다."));

        userCouponRepository.save(
                UserCoupon.create(command.userId(), coupon)
        );
        UserCoupon userCoupon = userCouponRepository.findBy(command.userId(), command.couponId())
                .orElse(
                        userCouponRepository.save(
                            UserCoupon.create(command.userId(), coupon)
                        )
                );

        if (userCoupon.isUsed()) {
            throw new CoreException(ErrorType.COUPON_ALREADY_USED);
        }

        return userCoupon;
    }
}

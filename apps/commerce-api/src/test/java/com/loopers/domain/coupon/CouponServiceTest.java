package com.loopers.domain.coupon;

import com.loopers.domain.like.Like;
import com.loopers.domain.like.LikeCommand;
import com.loopers.infrastructure.coupon.CouponJpaRepository;
import com.loopers.infrastructure.coupon.UserCouponJpaRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CouponServiceTest {
    @Autowired
    private CouponJpaRepository couponJpaRepository;
    @Autowired
    private UserCouponJpaRepository userCouponJpaRepository;
    @Autowired
    private CouponService couponService;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("쿠폰을 가져올 때")
    @Nested
    class GetCoupon {
        @Test
        @DisplayName("존재하는 쿠폰을 가져오면 성공 결과를 반환한다.")
        void getCoupon_whenCouponExists() {
            // given
            Long userId = 1L;
            Long couponId = 1L;
            Coupon coupon = couponJpaRepository.save(
                    Coupon.create(CouponCommand.Create.of(
                            "Test Coupon",
                            "This is a test coupon",
                            BigDecimal.valueOf(1000),
                            DiscountType.FIXED
                    ))
            );
            UserCoupon userCoupon = userCouponJpaRepository.save(
                    UserCoupon.create(userId, coupon)
            );

            CouponCommand.Get command = CouponCommand.Get.of(userId, couponId);

            // when
            UserCoupon result = couponService.getUserCoupon(command);

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result).isEqualTo(coupon)
            );
        }

        @Test
        @DisplayName("유저 쿠폰을 발급하지 않았으면, 유저 쿠폰을 새로 발급하고 가져온다")
        void getIssuedCoupon_whenUserCouponNoExists() {
            // given
            Long userId = 1L;
            Long couponId = 1L;
            Coupon coupon = couponJpaRepository.save(
                    Coupon.create(CouponCommand.Create.of(
                            "Test Coupon",
                            "This is a test coupon",
                            BigDecimal.valueOf(1000),
                            DiscountType.FIXED
                    ))
            );

            CouponCommand.Get command = CouponCommand.Get.of(userId, couponId);

            // when
            UserCoupon result = couponService.getUserCoupon(command);

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getUserId()).isEqualTo(userId),
                    () -> assertThat(result.getCouponId()).isEqualTo(coupon.getId()),
                    () -> assertThat(result.getType()).isEqualTo(coupon.getType()),
                    () -> assertThat(result.getValue().longValue()).isEqualTo(coupon.getValue().longValue()),
                    () -> assertThat(result.getIssuedAt()).isNotNull(),
                    () -> assertThat(result.getUsedAt()).isNull()
            );
        }

        @Test
        @DisplayName("쿠폰이 존재하지 않으면 NOT_FOUND에러가 발생한다")
        void throwNotFound_whenCouponNotExist() {
            // given
            Long userId = 1L;
            Long couponId = 1L;

            CouponCommand.Get command = CouponCommand.Get.of(userId, couponId);

            // when
            CoreException result = assertThrows(CoreException.class, () -> couponService.getUserCoupon(command));

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }

        @Test
        @DisplayName("유저 쿠폰이 이미 사용되었스면 COUPON_ALREADY_USED 에러가 발생한다")
        void throwCouponAlreadyUsed_whenUserCouponIsUsed() {
            // given
            Long userId = 1L;
            Long couponId = 1L;
            Coupon coupon = couponJpaRepository.save(
                    Coupon.create(CouponCommand.Create.of(
                            "Test Coupon",
                            "This is a test coupon",
                            BigDecimal.valueOf(1000),
                            DiscountType.FIXED
                    ))
            );
            UserCoupon userCoupon = userCouponJpaRepository.save(UserCoupon.create(userId, coupon));
            userCoupon.use(1L, LocalDateTime.now());
            userCouponJpaRepository.saveAndFlush(userCoupon);

            CouponCommand.Get command = CouponCommand.Get.of(userId, couponId);

            // when
            CoreException result = assertThrows(CoreException.class, () -> couponService.getUserCoupon(command));

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.COUPON_ALREADY_USED);
        }
    }
}

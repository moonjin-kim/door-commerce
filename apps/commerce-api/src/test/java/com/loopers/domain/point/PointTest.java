package com.loopers.domain.point;

import com.loopers.domain.user.User;
import com.loopers.fixture.UserFixture;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PointTest {

    @DisplayName("포인트 충전할 때")
    @Nested
    class Charge {
        @DisplayName("포인트 충전 시, 잔액이 포인트 충전 금액 만큼 추가된다.")
        @Test
        void chargePoint(){
            //given
            User user = UserFixture.createMember();
            int amount = 10000;

            Point point = Point.init(user.getId());
            point.charge(amount);

            //when
            point.charge(amount);

            //then
            assertThat(point.getBalance()).isEqualTo(20000);
        }

        @DisplayName("0 이하의 정수로 포인트를 충전 시 INVALID_POINT_AMOUNT 예외가 반환된다.")
        @Test
        void throwsInvalidPointAmountException_whenAmountLessThanZero(){
            //given
            User user = UserFixture.createMember();
            int amount = 0;

            Point point = Point.init(user.getId());

            //when
            CoreException result = assertThrows(CoreException.class, () -> {
                point.charge(amount);
            });

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.INVALID_POINT_AMOUNT);
        }
    }
}

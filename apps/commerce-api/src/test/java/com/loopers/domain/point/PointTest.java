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
            int balance = 0;

            //when
            Point point = Point.charge(user, amount, balance);

            //then
            assertThat(point.getAmount()).isEqualTo(amount);
            assertThat(point.getBalance()).isEqualTo(amount + balance);
            assertThat(point.getStatus()).isEqualTo(PointStatus.CHARGE);
        }

        @DisplayName("0 이하의 정수로 포인트를 충전 시 실패한다.")
        @Test
        void throwsBadRequestException_whenAmountLessThanZero(){
            //given
            User user = UserFixture.createMember();
            int amount = 0;
            int balance = 0;

            //when
            CoreException result = assertThrows(CoreException.class, () -> {
                Point.charge(user, amount, balance);
            });

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }
}

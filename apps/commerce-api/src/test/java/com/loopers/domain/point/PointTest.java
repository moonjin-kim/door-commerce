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

    @DisplayName("포인트를 사용할 때,")
    @Nested
    class Use {
        @DisplayName("잘못된 금액이 주어지면, INVALID_POINT_AMOUNT 예외가 발생한다.")
        @Test
        void throwsBadRequestException_whenPointAmountIsInvalid(){
            //given
            Point point = Point.init(1L);

            //when
            CoreException result = assertThrows(CoreException.class, () -> {
                point.use(0);
            });

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.INVALID_POINT_AMOUNT);
        }

        @DisplayName("잔액보다 많은 금액 사용요청이 발생하면, 예외가 발생한다.")
        @Test
        void throwsInsufficientBalance_whenBalanceLessThanPointAmount(){
            //given
            Point point = Point.init(1L);
            point.charge(1000L);

            //when
            CoreException result = assertThrows(CoreException.class, () -> {
                point.use(1001L);
            });

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.INSUFFICIENT_BALANCE);
        }

        @DisplayName("잔액보다 적은 포인트 사용을 요청하면, 잔액이 포인트만큼 감소한다.")
        @Test
        void lessPoint_whenCorrectPointAmountProvider(){
            //given
            Point point = Point.init(1L);
            point.charge(1000L);

            //when
            point.use(1000L);

            //then
            assertThat(point.getBalance()).isEqualTo(0L);
        }
    }
}

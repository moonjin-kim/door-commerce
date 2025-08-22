package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PointHistoryTest {
    @DisplayName("포인트 충전 기록 생성")
    @Nested
    class Charge {
        @DisplayName("충전 금액이 0 이하인 경우 INVALID_POINT_AMOUNT 예외 발생")
        @Test
        void throwInvalidPointAmount_chargeAmountZeroOrLess() {
            //given
            PointCommand.Charge command = new PointCommand.Charge(1L, 0L);

            //when
            CoreException exception = assertThrows(CoreException.class, () -> PointHistory.charge(1L, command));

            //then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.INVALID_POINT_AMOUNT);
        }

        @DisplayName("포인트 ID가 없는 경우 INVALID_INPUT예외 발생")
        @Test
        void throwInvalidPointAmount_pointIdNull() {
            //given
            PointCommand.Charge command = new PointCommand.Charge(1L, 0L);

            //when
            CoreException exception = assertThrows(CoreException.class, () -> PointHistory.charge(null, command));

            //then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.INVALID_POINT_AMOUNT);
        }

        @DisplayName("포인트 충전 기록 생성")
        @Test
        void returnPointHistory_whenCurrentValue() {
            //given
            PointCommand.Charge command = new PointCommand.Charge(1L, 1000L);

            //when
            PointHistory pointHistory = PointHistory.charge(1L, command);

            //then
            assertThat(pointHistory.getPointId()).isEqualTo(1L);
            assertThat(pointHistory.getAmount()).isEqualTo(1000L);
            assertThat(pointHistory.getStatus()).isEqualTo(PointStatus.CHARGE);
        }
    }

    @DisplayName("포인트 충전 기록 생성")
    @Nested
    class Use {
        @DisplayName("충전 금액이 0 이하인 경우 INVALID_POINT_AMOUNT 예외 발생")
        @Test
        void throwInvalidPointAmount_chargeAmountZeroOrLess() {
            //given
            PointCommand.Using command = new PointCommand.Using(1L, "123456", 0L);

            //when
            CoreException exception = assertThrows(CoreException.class, () -> PointHistory.use(1L, command));

            //then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.INVALID_POINT_AMOUNT);
        }

        @DisplayName("포인트 ID가 없는 경우 INVALID_INPUT예외 발생")
        @Test
        void throwInvalidPointAmount_pointIdNull() {
            //given
            PointCommand.Using command = new PointCommand.Using(1L, "123456", 1000L);

            //when
            CoreException exception = assertThrows(CoreException.class, () -> PointHistory.use(null, command));

            //then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("주문 ID가 없는 경우 BAD_REQUEST 예외 발생")
        @Test
        void returnPointHistory_whenCurrentValue() {
            //given
            PointCommand.Using command = new PointCommand.Using(1L, null, 1000L);

            //when
            CoreException exception = assertThrows(CoreException.class, () -> PointHistory.use(null, command));

            //then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

}

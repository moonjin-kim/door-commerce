package com.loopers.domain.order;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @DisplayName("돈을 더할 때")
    @Nested
    class plus {

        @DisplayName("올바른 값을 받으면, 새로운 Money 객체를 반환한다.")
        @Test
        void returnMoney_ValidAmount() {
            //given
            Money money = new Money(1000L);

            //when
            Money result = money.plus(500L);

            //then
            assertEquals(1500L, result.value());
        }

        @DisplayName("돈을 더할 때 음수는 허용하지 않는다.")
        @Test
        void throwBadRequest_whenRequestNagative() {
            //given
            Money money = new Money(1000L);

            //when
            CoreException exception = assertThrows(CoreException.class, () -> {
                money.plus(-500L);
            });

            //then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }
    }

    @DisplayName("돈을 뺄때")
    @Nested
    class minus {
        @DisplayName("올바른 값을 받으면, 새로운 Money 객체를 반환한다.")
        @Test
        void returnMoney_ValidAmount() {
            //given
            Money money = new Money(1000L);

            //when
            Money result = money.minus(500L);

            //then
            assertEquals(500L, result.value());
        }

        @DisplayName("음수를 받으면, InvalidInput 예외를 던진다.")
        @Test
        void throwBadRequest_whenRequestNagative() {
            //given
            Money money = new Money(1000L);

            //when
            CoreException exception = assertThrows(CoreException.class, () -> {
                money.minus(-500L);
            });

            //then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }

        @DisplayName("남은 돈보다 크면, InvalidInput 예외를 던진다.")
        @Test
        void throwBadRequest_whenMoreThanBalance() {
            //given
            Money money = new Money(1000L);

            //when
            CoreException exception = assertThrows(CoreException.class, () -> {
                money.minus(1001L);
            });

            //then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }
    }
}

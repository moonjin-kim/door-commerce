package com.loopers.domain.payment;

import com.loopers.infrastructure.payment.PaymentJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class PaymentServiceTest {
    @Autowired
    private PaymentJpaRepository paymentJpaRepository;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("결제 요청을 하면")
    @Nested
    class Pay {
        @DisplayName("결제 정보가 반환된다.")
        @Test
        void returnPaymentInfo_whenPaymentIsSuccessful() {
            // given
            PaymentCommand.Pay command = PaymentCommand.Pay.of("123456", 1000L, 10000L, PaymentType.POINT);

            // when
            PaymentInfo.Pay paymentInfo = paymentService.requestPayment(command);

            // then
            assertAll(
            () -> assertThat(paymentInfo).isNotNull(),
                    () -> assertThat(paymentInfo.paymentAmount().longValue()).isEqualTo(10000L),
                    () -> assertThat(paymentInfo.type()).isEqualTo(PaymentType.POINT)
            );
        }

        @DisplayName("결제 정보가 저장된다.")
        @Test
        void savedPaymentInfo_whenPaymentIsSuccessful() {
            // given
            PaymentCommand.Pay command = PaymentCommand.Pay.of("123456", 1000L, 10000L, PaymentType.POINT);

            // when
            PaymentInfo.Pay paymentInfo = paymentService.requestPayment(command);

            // then
            Payment payment = paymentJpaRepository.findById(paymentInfo.paymentId()).orElseThrow();
            assertAll(
                    () -> assertThat(payment).isNotNull(),
                    () -> assertThat(payment.getOrderId()).isEqualTo("123456"),
                    () -> assertThat(payment.getUserId()).isEqualTo(1000L),
                    () -> assertThat(payment.getPaymentAmount().longValue()).isEqualTo(10000L),
                    () -> assertThat(payment.getPaymentType()).isEqualTo(PaymentType.POINT),
                    () -> assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING)
            );
        }

        @DisplayName("결제 금액이 0 이하이면 IllegalArgumentException 예외가 발생한다.")
        @Test
        void throwException_whenPaymentAmountIsNegative() {
            // given
            PaymentCommand.Pay command = PaymentCommand.Pay.of("123456", 1000L, -1L, PaymentType.POINT);

            // when & then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                paymentService.requestPayment(command);
            });

            // then
            assertThat(exception.getMessage()).isEqualTo("결제 금액은 0 이상이여야 합니다.");

        }

        @DisplayName("주문 ID가 제공되지 않았으면 IllegalArgumentException 예외가 발생한다.")
        @Test
        void throwIllegalArgumentException_whenNotProviderOrderId() {
            // given
            PaymentCommand.Pay command = PaymentCommand.Pay.of(null, 1000L, 10000L, PaymentType.POINT);

            // when & then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                paymentService.requestPayment(command);
            });

            // then
            assertThat(exception.getMessage()).isEqualTo("Order ID는 null일 수 없습니다.");
        }

        @DisplayName("유저 ID가 제공되지 않았으면 IllegalArgumentException 예외가 발생한다.")
        @Test
        void throwIllegalArgumentException_whenNotProviderUserId() {
            // given
            PaymentCommand.Pay command = PaymentCommand.Pay.of("123456", null, 10000L, PaymentType.POINT);

            // when & then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                paymentService.requestPayment(command);
            });

            // then
            assertThat(exception.getMessage()).isEqualTo("User ID는 null일 수 없습니다.");

        }
    }
}

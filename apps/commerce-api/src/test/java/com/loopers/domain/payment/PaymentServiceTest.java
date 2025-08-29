package com.loopers.domain.payment;

import com.loopers.domain.order.OrderEvent;
import com.loopers.infrastructure.payment.PaymentJpaRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class PaymentServiceTest {
    @MockitoBean
    private PaymentEventPublisher paymentEventPublisher;
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
            PaymentInfo.Pay paymentInfo = paymentService.createPayment(command);

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
            PaymentInfo.Pay paymentInfo = paymentService.createPayment(command);

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
                paymentService.createPayment(command);
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
                paymentService.createPayment(command);
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
                paymentService.createPayment(command);
            });

            // then
            assertThat(exception.getMessage()).isEqualTo("User ID는 null일 수 없습니다.");

        }
    }

    @DisplayName("결제완료 처리시")
    @Nested
    class PaymentComplete {
        @DisplayName("결제가 완료 상태로 변경되고 transactionKey가 저장된다.")
        @Test
        void completePayment_whenPaymentIsSuccessful() {
            // given
            PaymentCommand.Pay command = PaymentCommand.Pay.of("order-001", 1000L, 5000L, PaymentType.POINT);
            paymentService.createPayment(command);

            // when
            String transactionKey = "tx-123";
            PaymentInfo.Pay paymentInfo = paymentService.paymentComplete("order-001", transactionKey);

            // then
            Payment payment = paymentJpaRepository.findById(paymentInfo.paymentId()).orElseThrow();
            assertAll(
                    () -> assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED),
                    () -> assertThat(payment.getTransactionKey()).isEqualTo(transactionKey),
                    () -> assertThat(paymentInfo.type()).isEqualTo(PaymentType.POINT)
            );
        }

        @DisplayName("결제 완료가 성공하면 결제 완료 이벤트가 발행된다.")
        @Test
        void publishCompleteEvent_whenPaymentIsSuccessful() {
            // given
            PaymentCommand.Pay command = PaymentCommand.Pay.of("order-001", 1000L, 5000L, PaymentType.POINT);
            paymentService.createPayment(command);
            doNothing().when(paymentEventPublisher).publish(any(PaymentEvent.Success.class));

            // when
            String transactionKey = "tx-123";
            PaymentInfo.Pay paymentInfo = paymentService.paymentComplete("order-001", transactionKey);

            // then
            Payment payment = paymentJpaRepository.findById(paymentInfo.paymentId()).orElseThrow();
            assertAll(
                    () -> assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED),
                    () -> assertThat(payment.getTransactionKey()).isEqualTo(transactionKey),
                    () -> assertThat(paymentInfo.type()).isEqualTo(PaymentType.POINT)
            );
            verify(paymentEventPublisher).publish(any(PaymentEvent.Success.class));
        }

        @DisplayName("존재하지 않는 주문 ID로 결제 완료 처리 시 NOT_FOUND 예외가 발생한다.")
        @Test
        void throwException_whenOrderIdNotFound() {
            doNothing().when(paymentEventPublisher).publish(any(PaymentEvent.Success.class));
            // when
            CoreException exception = assertThrows(CoreException.class, () -> {
                paymentService.paymentComplete("not-exist-order", "tx-999");
            });
            //then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
            verify(paymentEventPublisher, never()).publish(any(PaymentEvent.Success.class));
        }
    }

    @DisplayName("결제 실패 처리 시")
    @Nested
    class PaymentFail {
        @DisplayName("결제가 실패 상태로 변경되고 실패 사유가 저장된다.")
        @Test
        void failPayment_whenPaymentIsFailed() {
            // given
            PaymentCommand.Pay command = PaymentCommand.Pay.of("order-002", 2000L, 7000L, PaymentType.POINT);
            paymentService.createPayment(command);
            doNothing().when(paymentEventPublisher).publish(any(PaymentEvent.Failed.class));

            // when
            String reason = "잔액 부족";
            PaymentInfo.Pay paymentInfo = paymentService.paymentFail("order-002", reason);

            // then
            Payment payment = paymentJpaRepository.findById(paymentInfo.paymentId()).orElseThrow();
            assertAll(
                    () -> assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED),
                    () -> assertThat(payment.getFailureReason()).isEqualTo(reason),
                    () -> assertThat(paymentInfo.type()).isEqualTo(PaymentType.POINT)
            );
            verify(paymentEventPublisher).publish(any(PaymentEvent.Failed.class));
        }

        @DisplayName("존재하지 않는 주문 ID로 결제 실패 처리 시 NOT_FOUND 예외가 발생한다.")
        @Test
        void throwException_whenOrderIdNotFound() {
            doNothing().when(paymentEventPublisher).publish(any(PaymentEvent.Failed.class));
            // when
            CoreException exception = assertThrows(CoreException.class, () -> {
                paymentService.paymentFail("not-exist-order", "실패 사유");
            });
            // then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
            verify(paymentEventPublisher, never()).publish(any(PaymentEvent.Failed.class));
        }
    }
}

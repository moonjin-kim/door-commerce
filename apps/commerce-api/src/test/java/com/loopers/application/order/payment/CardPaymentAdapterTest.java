package com.loopers.application.order.payment;

import com.loopers.domain.payment.PaymentInfo;
import com.loopers.domain.payment.PaymentStatus;
import com.loopers.domain.payment.PaymentType;
import com.loopers.domain.pg.CardType;
import com.loopers.domain.pg.PgProcess;
import com.loopers.infrastructure.payment.PaymentResponse;
import com.loopers.infrastructure.pg.PgResponse;
import com.loopers.utils.DatabaseCleanUp;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
class CardPaymentAdapterTest {

    @Autowired
    CardPaymentAdapter adapter;

    @MockitoBean
    PgProcess pgProcess;

    @Autowired
    DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("PG로의 결제를 요청할 때,")
    @Nested
    class Payment {
        @DisplayName("결제 요청이 성공하면, 결제 정보를 반환한다.")
        @Test
        void pay_success() {
            PaymentCriteria.Pay criteria = PaymentCriteria.Pay.of(
                    "order123",
                    1L,
                    10000L,
                    PaymentType.CARD.name(),
                    CardType.KB,
                    "1234-1234-1234-1234"
            );
            PgResponse.Pay paymentResponse = mock(PgResponse.Pay.class);

            // PG 결제 정상 동작 모킹
            when(pgProcess.payment(any(), any())).thenReturn(paymentResponse);

            PaymentInfo.Pay result = adapter.pay(criteria);

            assertThat(result.paymentAmount()).isEqualTo(BigDecimal.valueOf(criteria.amount()));
            assertThat(result.status()).isEqualTo(PaymentStatus.PENDING);
            verify(pgProcess).payment(any(), any());
        }

        @DisplayName("결제 요청이 실패하면, 결제 정보를 준비 상태로 반환한다.")
        @Test
        void pay_pgFail() {
            PaymentCriteria.Pay criteria = PaymentCriteria.Pay.of(
                    "order123",
                    1L,
                    10000L,
                    PaymentType.CARD.name(),
                    CardType.KB,
                    "1234-1234-1234-1234"
            );

            // PG 결제 실패(예외 발생) 모킹
            when(pgProcess.payment(any(), any())).thenThrow(new RuntimeException("PG 오류"));

            PaymentInfo.Pay result = adapter.pay(criteria);

            assertThat(result.status()).isEqualTo(PaymentStatus.PENDING);
            verify(pgProcess).payment(any(), any());
        }

        @DisplayName("PG 결제 요청이 BadRequest로 실패하면, 결제 실패 정보를 반환한다.")
        @Test
        void pay_pgFailBadRequest() {
            Request request = Request.create(Request.HttpMethod.POST, "/pg/pay", java.util.Collections.emptyMap(), null, null, null);
            PaymentCriteria.Pay criteria = PaymentCriteria.Pay.of(
                    "order123",
                    1L,
                    10000L,
                    PaymentType.CARD.name(),
                    CardType.KB,
                    "1234-1234-1234-1234"
            );

            // PG 결제 실패(예외 발생) 모킹
            when(pgProcess.payment(any(), any())).thenThrow(new FeignException.BadRequest("PG 오류", request, null, null));

            PaymentInfo.Pay result = adapter.pay(criteria);

            assertThat(result.status()).isEqualTo(PaymentStatus.FAILED);
            verify(pgProcess).payment(any(), any());
        }
    }

}

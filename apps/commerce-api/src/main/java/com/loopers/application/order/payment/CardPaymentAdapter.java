package com.loopers.application.order.payment;

import com.loopers.infrastructure.pg.PgRequest;
import com.loopers.domain.pg.PgProcess;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentInfo;
import com.loopers.domain.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("CARD")
@RequiredArgsConstructor
public class CardPaymentAdapter implements PaymentMethod {
    private final PgProcess pgProcess;
    private final PaymentService paymentService;

    @Override
    public PaymentInfo.Pay pay(PaymentCriteria.Pay criteria) {
        String callbackUrl = "http://localhost:8080/api/v1/orders/callback";

        PaymentInfo.Pay paymentResult = paymentService.pay(PaymentCommand.Pay.of(
                criteria.orderId(),
                criteria.userId(),
                criteria.amount(),
                criteria.method(),
                criteria.cardType(),
                criteria.cardNumber()
        ));

        try {
            pgProcess.payment(
                    PgRequest.Pay.from(
                            criteria,
                            callbackUrl
                    ),
                    criteria.userId()
            );
        } catch (Exception e) {
            log.error("Payment failed for orderId: {}", criteria.orderId(), e);
            paymentResult = paymentService.paymentFail(criteria.orderId(), e.getMessage());
        }

        return paymentResult;
    }
}

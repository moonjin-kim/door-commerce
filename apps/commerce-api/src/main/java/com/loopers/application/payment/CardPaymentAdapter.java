package com.loopers.application.payment;

import com.loopers.application.payment.pg.PgCommand;
import com.loopers.application.payment.pg.PgProcess;
import com.loopers.application.payment.pg.PgResult;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentInfo;
import com.loopers.domain.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("CARD")
@RequiredArgsConstructor
public class CardPaymentAdapter implements PaymentMethod {
    private final PgProcess pgProcess;
    private final PaymentService paymentService;

    @Override
    public PaymentInfo.Pay pay(PaymentCriteria.Pay criteria) {
        String callbackUrl = "http://localhost:8080/api/v1/orders/callback";

        PgResult.Pay pgResult = pgProcess.payment(
                PgCommand.Pay.from(
                        criteria,
                        callbackUrl
                ),
                criteria.userId()
        );

        return paymentService.pay(PaymentCommand.Pay.of(
                criteria.orderId(),
                criteria.userId(),
                criteria.amount(),
                criteria.method(),
                criteria.cardType(),
                criteria.cardNumber(),
                pgResult.transactionKey()
        ));
    }
}

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
    public PaymentInfo.Pay pay(PaymentCommand.Pay command) {
        String callbackUrl = "http://localhost:8080/payment/callback";
        PgResult.Pay pgResult = pgProcess.payment(
                PgCommand.Pay.from(
                        command,
                        callbackUrl
                ),
                command.userId()
        );
        System.out.println(pgResult);

        return paymentService.pay(command);
    }
}

package com.loopers.application.payment;

import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentInfo;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponseException;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentProcess {
    private final Map<String, PaymentMethod> paymentStrategyMap;

    public PaymentInfo.Pay processPayment(PaymentCommand.Pay command) {
        PaymentMethod paymentMethod = paymentStrategyMap.get(command.method());
        if (paymentMethod == null) {
            throw new CoreException(ErrorType.UNSUPPORTED_PAYMENT_METHOD);
        }

        return paymentMethod.pay(command);
    }
}

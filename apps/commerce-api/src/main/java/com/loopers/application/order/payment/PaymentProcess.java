package com.loopers.application.order.payment;

import com.loopers.domain.payment.PaymentInfo;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentProcess {
    private final Map<String, PaymentMethod> paymentStrategyMap;

    public PaymentInfo.Pay processPayment(PaymentCriteria.Pay command) {

        PaymentMethod paymentMethod = paymentStrategyMap.get(command.method().name());
        if (paymentMethod == null) {
            throw new CoreException(ErrorType.UNSUPPORTED_PAYMENT_METHOD);
        }

        return paymentMethod.pay(command);
    }
}

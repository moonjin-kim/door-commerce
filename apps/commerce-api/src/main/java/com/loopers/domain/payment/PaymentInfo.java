package com.loopers.domain.payment;

import java.math.BigDecimal;

public class PaymentInfo {
    public record Pay(
            Long paymentId,
            BigDecimal paymentAmount,
            PaymentType type,
            PaymentStatus status
    ){
        static public Pay from(Payment payment) {
            return new Pay(
                    payment.getId(),
                    payment.getPaymentAmount().value(),
                    payment.getPaymentType(),
                    payment.getStatus()
            );
        }

    }
}

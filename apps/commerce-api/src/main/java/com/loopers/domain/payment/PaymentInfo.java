package com.loopers.domain.payment;

public class PaymentInfo {
    public record Pay(
            Long paymentId,
            Long paymentAmount,
            PaymentType type
    ){
        static public Pay from(Payment payment) {
            return new Pay(
                    payment.getId(),
                    payment.getPaymentAmount().value(),
                    payment.getPaymentType()
            );
        }

    }
}

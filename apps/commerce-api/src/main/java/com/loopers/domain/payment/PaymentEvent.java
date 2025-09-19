package com.loopers.domain.payment;

public class PaymentEvent {
    public record Success(Long paymentId, String orderId, String type) {

        public static Success from(Payment payment) {
            return new Success(payment.getId(), payment.getOrderId(), payment.getPaymentType().name());
        }
    }

    public record Failed(Long paymentId, String orderId) {

        public static Failed from(Payment payment) {
            return new Failed(payment.getId(), payment.getOrderId());
        }
    }

}

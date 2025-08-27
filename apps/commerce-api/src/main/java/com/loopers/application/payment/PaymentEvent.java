package com.loopers.application.payment;

public class PaymentEvent {
    public record Failed(String orderId) {
        public static Failed of(String orderId) {
            return new Failed(orderId);
        }
    }

    public record Success(String orderId) {
        public static Success of(String orderId) {
            return new Success(orderId);
        }
    }
}

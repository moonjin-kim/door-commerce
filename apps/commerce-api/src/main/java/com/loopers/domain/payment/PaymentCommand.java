package com.loopers.domain.payment;

public class PaymentCommand {
    public record Pay(
            String orderId,
            Long userId,
            Long amount,
            String method,
            com.loopers.application.payment.pg.CardType cardType,
            String cardNumber
    ) {
        static public Pay of(String orderId, Long userId, Long amount, String method, com.loopers.application.payment.pg.CardType cardType, String cardNumber) {
            return new Pay(orderId, userId, amount, method, cardType, cardNumber);
        }

        static public Pay of(String orderId, Long userId, Long amount, String method) {
            return new Pay(orderId, userId, amount, method, null,null);
        }
    }
}

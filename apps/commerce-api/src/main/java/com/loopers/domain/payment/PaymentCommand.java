package com.loopers.domain.payment;

public class PaymentCommand {
    public record Pay(
            String orderId,
            Long userId,
            Long amount,
            String method,
            com.loopers.application.payment.pg.CardType cardType,
            String cardNumber,
            String transactionKey
    ) {
        static public Pay of(String orderId, Long userId, Long amount, String method) {
            return new Pay(orderId, userId, amount, method, null,null, null);
        }

        static public Pay of(
                String orderId,
                Long userId,
                Long amount,
                String method,
                com.loopers.application.payment.pg.CardType cardType,
                String cardNumber,
                String transactionKey
        ) {
            return new Pay(orderId, userId, amount, method, cardType, cardNumber, transactionKey);
        }
    }
}

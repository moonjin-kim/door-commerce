package com.loopers.domain.payment;

import com.loopers.application.payment.PaymentMethod;

public class PaymentCommand {
    public record Pay(
            Long orderId,
            Long userId,
            Long amount,
            String method,
            CardType cardType,
            String cardNumber
    ) {
        static public Pay of(Long orderId, Long userId, Long amount, String method, String cardType, String cardNumber) {
            return new Pay(orderId, userId, amount, method, CardType.valueOf(cardType),cardNumber);
        }

        static public Pay of(Long orderId, Long userId, Long amount, String method) {
            return new Pay(orderId, userId, amount, method, null,null);
        }
    }
}

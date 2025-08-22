package com.loopers.domain.payment;

import com.loopers.domain.pg.CardType;

public class PaymentCommand {
    public record Pay(
            String orderId,
            Long userId,
            Long amount,
            PaymentType method,
            CardType cardType,
            String cardNumber
    ) {
        static public Pay of(String orderId, Long userId, Long amount, PaymentType method) {
            return new Pay(orderId, userId, amount, method, null,null);
        }

        static public Pay of(
                String orderId,
                Long userId,
                Long amount,
                PaymentType method,
                CardType cardType,
                String cardNumber
        ) {
            return new Pay(orderId, userId, amount, method, cardType, cardNumber);
        }
    }
}

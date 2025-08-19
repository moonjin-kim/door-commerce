package com.loopers.application.payment;

import com.loopers.application.payment.pg.CardType;
import com.loopers.application.payment.pg.PgCommand;
import com.loopers.domain.payment.PaymentCommand;

public class PaymentCriteria {
    public record Pay(
            String orderId,
            Long userId,
            Long amount,
            String method,
            com.loopers.application.payment.pg.CardType cardType,
            String cardNumber
    ) {
        static public PaymentCriteria.Pay of(
                String orderId,
                Long userId,
                Long amount,
                String method,
                com.loopers.application.payment.pg.CardType cardType,
                String cardNumber
        ) {
            return new PaymentCriteria.Pay(orderId, userId, amount, method, cardType, cardNumber);
        }

        static public PaymentCriteria.Pay of(String orderId, Long userId, Long amount, String method) {
            return new PaymentCriteria.Pay(orderId, userId, amount, method, null,null);
        }
    }
}

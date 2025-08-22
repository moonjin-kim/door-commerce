package com.loopers.application.order.payment;

import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentType;
import com.loopers.domain.pg.CardType;

public class PaymentCriteria {
    public record Pay(
            String orderId,
            Long userId,
            Long amount,
            PaymentType method,
            CardType cardType,
            String cardNumber
    ) {
        static public PaymentCriteria.Pay of(
                String orderId,
                Long userId,
                Long amount,
                String method,
                CardType cardType,
                String cardNumber
        ) {
            return new PaymentCriteria.Pay(
                    orderId,
                    userId,
                    amount,
                    PaymentType.of(method),
                    cardType,
                    cardNumber
            );
        }

        static public PaymentCriteria.Pay of(String orderId, Long userId, Long amount, String method) {
            return new PaymentCriteria.Pay(orderId, userId, amount, PaymentType.of(method), null,null);
        }

        public PaymentCommand.Pay toCommand() {
            return PaymentCommand.Pay.of(
                    orderId(),
                    userId(),
                    amount(),
                    method(),
                    cardType(),
                    cardNumber()
            );
        }
    }
}

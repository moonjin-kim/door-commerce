package com.loopers.infrastructure.payment;

import com.loopers.application.order.payment.PaymentCriteria;
import com.loopers.domain.pg.CardType;

public class PgCommand {
    public record Pay(
            String orderId,
            CardType cardType,
            String cardNo,
            Long amount,
            String callbackUrl
    ) {
        public static Pay of(
                String orderId,
                CardType cardType,
                String cardNumber,
                Long amount,
                String callbackUrl
        ) {

            return new Pay(
                    orderId,
                    cardType,
                    cardNumber,
                    amount,
                    callbackUrl
            );
        }

        public static Pay from(PaymentCriteria.Pay command, String callbackUrl) {
            return new Pay(
                    String.valueOf(command.orderId()),
                    CardType.valueOf(command.cardType().name()),
                    command.cardNumber(),
                    command.amount(),
                    callbackUrl

            );
        }
    }
}

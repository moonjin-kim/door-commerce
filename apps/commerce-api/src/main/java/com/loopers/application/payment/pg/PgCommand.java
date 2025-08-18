package com.loopers.application.payment.pg;

import com.loopers.application.payment.PaymentCriteria;
import com.loopers.domain.payment.PaymentCommand;

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

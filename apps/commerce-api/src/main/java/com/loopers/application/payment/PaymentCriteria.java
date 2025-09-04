package com.loopers.application.payment;

import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentType;
import com.loopers.domain.pg.CardType;
import com.loopers.domain.order.OrderEvent;

public class PaymentCriteria {
    public record RequestPayment(
            String orderId,
            Long userId,
            Long amount,
            PaymentType method,
            CardType cardType,
            String cardNumber
    ) {
        static public RequestPayment of(
                String orderId,
                Long userId,
                Long amount,
                String method,
                CardType cardType,
                String cardNumber
        ) {
            return new RequestPayment(
                    orderId,
                    userId,
                    amount,
                    PaymentType.of(method),
                    cardType,
                    cardNumber
            );
        }

        static public RequestPayment of(String orderId, Long userId, Long amount, String method) {
            return new RequestPayment(orderId, userId, amount, PaymentType.of(method), null,null);
        }

        static public RequestPayment from(OrderEvent.RequestPayment event) {
            return new RequestPayment(
                    event.orderId(),
                    event.userId(),
                    event.amount(),
                    PaymentType.of(event.paymentMethodType()),
                    CardType.valueOf(event.cardType()),
                    event.cardNumber()
            );
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

    public record Callback(
            String transactionKey,
            String orderId,
            CardType cardType,
            String cardNo,
            String amount,
            String transactionStatus,
            String reason
    ) {
        public static PaymentCriteria.Callback of(
                String transactionKey,
                String orderId,
                CardType cardType,
                String cardNo,
                String amount,
                String transactionStatus,
                String reason
        ) {
            return new PaymentCriteria.Callback(transactionKey, orderId, cardType, cardNo, amount, transactionStatus, reason);
        }
    }
}

package com.loopers.interfaces.api.payment;

import com.loopers.application.order.OrderCriteria;
import com.loopers.application.payment.PaymentCriteria;
import com.loopers.application.payment.PaymentMethodType;
import com.loopers.domain.pg.CardType;

import java.util.List;
import java.util.stream.Collectors;

public class PaymentV1Request {
    public record Callback(
            String transactionKey,
            String orderId,
            CardType cardType,
            String cardNo,
            String amount,
            String transactionStatus,
            String reason
    ) {
        public PaymentCriteria.Callback toCriteria() {
            return PaymentCriteria.Callback.of(
                    transactionKey,
                    orderId,
                    cardType,
                    cardNo,
                    amount,
                    transactionStatus,
                    reason
            );
        }
    }
}


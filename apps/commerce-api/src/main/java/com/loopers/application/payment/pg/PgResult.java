package com.loopers.application.payment.pg;

import com.loopers.application.order.OrderCriteria;

public class PgResult {
    public record Pay(
            String transactionKey,
            String status
    ) {

    }

    public record Find(
            String transactionKey,
            String orderId,
            CardType cardType,
            String cardNo,
            String amount,
            String status,
            String reason
    ) {
    }
}

package com.loopers.infrastructure.pg;

import com.loopers.domain.pg.CardType;

public class PgResponse {
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

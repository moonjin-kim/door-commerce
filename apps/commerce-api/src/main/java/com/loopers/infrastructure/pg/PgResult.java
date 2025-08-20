package com.loopers.infrastructure.pg;

import com.loopers.domain.pg.CardType;

public class PgResult {
    public record Pay(
            String transactionKey,
            String status
    ) {
        static PgResult.Pay from(PgResponse.Pay response) {
            return new PgResult.Pay(response.transactionKey(), response.status());
        }
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
        static PgResult.Find from(PgResponse.Find response) {
            return new PgResult.Find(
                    response.transactionKey(),
                    response.orderId(),
                    response.cardType(),
                    response.cardNo(),
                    response.amount(),
                    response.status(),
                    response.reason()
            );
        }
    }
}

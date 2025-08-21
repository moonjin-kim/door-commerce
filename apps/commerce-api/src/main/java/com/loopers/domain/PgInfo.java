package com.loopers.domain;

import com.loopers.domain.pg.CardType;
import com.loopers.infrastructure.pg.PgResponse;

public class PgInfo {
    public record Pay(
            String transactionKey,
            String status
    ) {
        static public PgInfo.Pay from(PgResponse.Pay response) {
            return new PgInfo.Pay(response.transactionKey(), response.status());
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
        static public PgInfo.Find from(PgResponse.Find response) {
            return new PgInfo.Find(
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

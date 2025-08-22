package com.loopers.infrastructure.pg;

import com.loopers.domain.pg.CardType;

import java.util.List;

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

    public record FindByOrderId(
            String orderId,
            List<Transactional> transactions
    ) {
        public static FindByOrderId of(String orderId, List<Transactional> transactions) {
            return new FindByOrderId(orderId, transactions != null ? transactions : List.of());
        }
    }

    public record Transactional(
            String transactionKey,
            String status,
            String reason
    ) {}
}

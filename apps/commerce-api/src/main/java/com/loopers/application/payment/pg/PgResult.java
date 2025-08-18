package com.loopers.application.payment.pg;

public class PgResult {
    public record Pay(
            String transactionKey,
            String status
    ) {

    }

    public record Find(){

    }
}

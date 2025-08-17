package com.loopers.application.payment.pg;

public class PgResult {
    record Pay(
            String transactionKey,
            String status
    ) {

    }

    record Find(){

    }
}

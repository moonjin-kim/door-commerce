package com.loopers.application.payment.pg;

public interface PgProcess {
    PgResult.Pay payment(PgCommand.Pay command, Long userId);

    PgResult.Find findByOrderId(String orderId, Long userId);

    PgResult.Find findByPGId(String paymentId, Long userId);
}

package com.loopers.domain.pg;

import com.loopers.infrastructure.payment.PgCommand;
import com.loopers.infrastructure.payment.PgResult;

public interface PgProcess {
    PgResult.Pay payment(PgCommand.Pay command, Long userId);

    PgResult.Find findByOrderId(String orderId, Long userId);

    PgResult.Find findByPGId(String paymentId, Long userId);
}

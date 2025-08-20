package com.loopers.domain.pg;

import com.loopers.infrastructure.pg.PgRequest;
import com.loopers.infrastructure.pg.PgResult;

import java.util.List;

public interface PgProcess {
    PgResult.Pay payment(PgRequest.Pay command, Long userId);

    PgResult.Find findByPGId(String paymentId, Long userId);

    List<PgResult.Find> findByOrderId(String orderId, Long userId);
}

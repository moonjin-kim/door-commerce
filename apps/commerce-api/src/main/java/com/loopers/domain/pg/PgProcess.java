package com.loopers.domain.pg;

import com.loopers.infrastructure.pg.PgRequest;
import com.loopers.infrastructure.pg.PgResponse;

import java.util.List;

public interface PgProcess {
    PgResponse.Pay payment(PgRequest.Pay command, Long userId);

    PgResponse.Find findByPGId(String paymentId, Long userId);

    PgResponse.FindByOrderId findByOrderId(String orderId, Long userId);
}

package com.loopers.domain.pg;

import com.loopers.infrastructure.pg.PgRequest;
import com.loopers.domain.PgInfo;
import com.loopers.infrastructure.pg.PgResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PgService {
    private final PgProcess pgProcess;

    public PgInfo.Pay payment(PgRequest.Pay command, Long userId) {
        PgResponse.Pay response = pgProcess.payment(command, userId);

        return PgInfo.Pay.from(response);
    }

    public PgInfo.Find findByTransactionKey(String transactionKey, Long userId) {
        PgResponse.Find response = pgProcess.findByPGId(transactionKey, userId);

        return PgInfo.Find.from(response);
    }

    public PgInfo.FindByOrderId findByOrderId(String orderId, Long userId) {
        PgResponse.FindByOrderId responses = pgProcess.findByOrderId(orderId, userId);

        return PgInfo.FindByOrderId.from(responses);
    }
}

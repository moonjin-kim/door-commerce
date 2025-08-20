package com.loopers.domain.pg;

import com.loopers.infrastructure.pg.PgRequest;
import com.loopers.infrastructure.pg.PgResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PgService {
    private final PgProcess pgProcess;

    public PgResult.Pay payment(PgRequest.Pay command, Long userId) {
        return pgProcess.payment(command, userId);
    }

    public PgResult.Find findByTransactionKey(String transactionKey, Long userId) {
        return pgProcess.findByPGId(transactionKey, userId);
    }

    public List<PgResult.Find> findByOrderId(String orderId, Long userId) {
        return pgProcess.findByOrderId(orderId, userId);
    }
}

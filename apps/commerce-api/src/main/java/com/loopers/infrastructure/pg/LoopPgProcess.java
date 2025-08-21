package com.loopers.infrastructure.pg;

import com.loopers.domain.pg.PgProcess;
import com.loopers.infrastructure.payment.PaymentResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoopPgProcess implements PgProcess {
    private final LoopFeignClient pgFeignClient;

    @CircuitBreaker(name = "pgCircuit")
    @Retry(name = "pgRetry")
    @Override
    public PgResult.Pay payment(PgRequest.Pay request, Long userId) {
        log.info("Payment request: {}", request.orderId());
        return PgResult.Pay.from(
                pgFeignClient.payment(request, userId).getData()
        );
    }


    @CircuitBreaker(name = "pgCircuit")
    @Retry(name = "pgRetry")
    @Override
    public List<PgResult.Find> findByOrderId(String orderId, Long userId) {
        PaymentResponse<List<PgResponse.Find>> result = pgFeignClient.findByOrderId(orderId, userId);
        return result.getData().stream().map(
                PgResult.Find::from
        ).toList();
    }

    @CircuitBreaker(name = "pgCircuit")
    @Retry(name = "pgRetry")
    @Override
    public PgResult.Find findByPGId(String paymentId, Long userId) {
        PaymentResponse<PgResponse.Find> result = pgFeignClient.findByPaymentId(paymentId, userId);
        return PgResult.Find.from(result.getData());
    }
}

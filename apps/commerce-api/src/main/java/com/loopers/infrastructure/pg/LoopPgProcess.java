package com.loopers.infrastructure.pg;

import com.loopers.domain.pg.PgProcess;
import com.loopers.infrastructure.payment.PaymentResponse;
import feign.FeignException;
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
    public PgResponse.Pay payment(PgRequest.Pay request, Long userId) {
        return pgFeignClient.payment(request, userId).getData();
    }

    @CircuitBreaker(name = "pgCircuit")
    @Retry(name = "pgRetry")
    @Override
    public PgResponse.FindByOrderId findByOrderId(String orderId, Long userId) {
        try {
            PaymentResponse<PgResponse.FindByOrderId> result = pgFeignClient.findByOrderId(orderId, userId);
            return result.getData();
        } catch (FeignException.NotFound e) {
            return PgResponse.FindByOrderId.of(orderId, List.of());
        }
    }

    @CircuitBreaker(name = "pgCircuit")
    @Retry(name = "pgRetry")
    @Override
    public PgResponse.Find findByPGId(String paymentId, Long userId) {
        PaymentResponse<PgResponse.Find> result = pgFeignClient.findByPaymentId(paymentId, userId);
        return result.getData();
    }
}

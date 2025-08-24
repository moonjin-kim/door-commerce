package com.loopers.infrastructure.pg;

import com.loopers.domain.pg.PgProcess;
import com.loopers.infrastructure.payment.PaymentResponse;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
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

    @CircuitBreaker(name = "pgCircuit", fallbackMethod = "fallbackPayment")
    @Override
    public PgResponse.Pay payment(PgRequest.Pay request, Long userId) {
        return pgFeignClient.payment(request, userId).getData();
    }

    @CircuitBreaker(name = "pgCircuit", fallbackMethod = "fallbackPayment")
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

    @CircuitBreaker(name = "pgCircuit", fallbackMethod = "fallbackPayment")
    @Retry(name = "pgRetry")
    @Override
    public PgResponse.Find findByPGId(String paymentId, Long userId) {
        PaymentResponse<PgResponse.Find> result = pgFeignClient.findByPaymentId(paymentId, userId);
        return result.getData();
    }

    private PgResponse.Pay fallbackPayment(PgRequest.Pay request, Long userId, Throwable throwable) {
        log.error("PG 결제 요청 실패: {}", throwable.getMessage());
        throw new CoreException(ErrorType.PG_ERROR, "PG 결제 요청에 실패했습니다. 잠시 후 다시 시도해주세요.");
    }
}

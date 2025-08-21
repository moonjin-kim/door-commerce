package com.loopers.infrastructure.pg;

import com.loopers.infrastructure.payment.PaymentResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "pg", url = "${pg.api.url}")
public interface LoopFeignClient {

    @PostMapping("/api/v1/payments")
    PaymentResponse<PgResponse.Pay> payment(@RequestBody PgRequest.Pay command, @RequestHeader("X-USER-ID") Long userId);

    @GetMapping("/api/v1/payments/{paymentId}")
    PaymentResponse<PgResponse.Find> findByPaymentId(@PathVariable("paymentId") String paymentId, @RequestHeader("X-USER-ID") Long userId);

    @GetMapping("/api/v1/payments")
    PaymentResponse<List<PgResponse.Find>> findByOrderId(@RequestParam("orderId") String orderId, @RequestHeader("X-USER-ID") Long userId);
}

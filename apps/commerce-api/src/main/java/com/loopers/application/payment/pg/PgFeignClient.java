package com.loopers.application.payment.pg;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "pg", url = "${pg.api.url}")
public interface PgFeignClient {
    @PostMapping("/api/v1/payments")
    PaymentResponse<PgResult.Pay> payment(@RequestBody PgCommand.Pay command, @RequestHeader("X-USER-ID") Long userId) ;

    @GetMapping("/api/v1/payments/{paymentId}")
    PaymentResponse<PgResult.Find> findByPaymentId(@PathVariable("paymentId") String paymentId, @RequestHeader("X-USER-ID") Long userId) ;

    @GetMapping("/api/v1/payments")
    PaymentResponse<PgResult.Find> findByOrderId(@RequestParam("orderId") String orderId, @RequestHeader("X-USER-ID") Long userId) ;
}

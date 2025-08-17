package com.loopers.application.payment.pg;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "pg", url = "${pg.api.url}")
public interface PgFeignClient {
    @PostMapping("/api/v1/payments")
    PgResult.Pay payment(@RequestBody PgCommand.Pay command) ;

    @GetMapping("/api/v1/payments/{paymentId}")
    PgResult.Find findByPaymentId(@PathVariable("paymentId") String paymentId) ;

    @GetMapping("/api/v1/payments")
    PgResult.Find findByOrderId(@RequestParam("orderId") String orderId) ;
}

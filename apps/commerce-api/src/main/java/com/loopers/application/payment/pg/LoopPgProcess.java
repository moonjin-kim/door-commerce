package com.loopers.application.payment.pg;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoopPgProcess implements PgProcess {
    private final PgFeignClient pgFeignClient;

    @Override
    public PgResult.Pay payment(PgCommand.Pay request) {
        return pgFeignClient.payment(request);
    }

    @Override
    public PgResult.Find findByOrderId(String orderId) {
        return pgFeignClient.findByOrderId(orderId);
    }

    @Override
    public PgResult.Find findByPGId(String paymentId) {
        return pgFeignClient.findByPaymentId(paymentId);
    }
}

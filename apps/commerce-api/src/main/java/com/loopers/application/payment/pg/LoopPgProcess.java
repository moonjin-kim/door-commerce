package com.loopers.application.payment.pg;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoopPgProcess implements PgProcess {
    private final PgFeignClient pgFeignClient;

    @Override
    public PgResult.Pay payment(PgCommand.Pay request, Long userId) {
        return pgFeignClient.payment(request, userId).getData();
    }

    @Override
    public PgResult.Find findByOrderId(String orderId, Long userId) {
        PaymentResponse<PgResult.Find> result = pgFeignClient.findByOrderId(orderId, userId);
        return result.getData();
    }

    @Override
    public PgResult.Find findByPGId(String paymentId, Long userId) {
        return pgFeignClient.findByPaymentId(paymentId, userId).getData();
    }
}

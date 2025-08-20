package com.loopers.infrastructure.pg;

import com.loopers.domain.pg.PgProcess;
import com.loopers.infrastructure.payment.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LoopPgProcess implements PgProcess {
    private final LoopFeignClient pgFeignClient;

    @Override
    public PgResult.Pay payment(PgRequest.Pay request, Long userId) {
        return PgResult.Pay.from(
                pgFeignClient.payment(request, userId).getData()
        );
    }

    @Override
    public List<PgResult.Find> findByOrderId(String orderId, Long userId) {
        PaymentResponse<List<PgResponse.Find>> result = pgFeignClient.findByOrderId(orderId, userId);
        return result.getData().stream().map(
                PgResult.Find::from
        ).toList();
    }

    @Override
    public PgResult.Find findByPGId(String paymentId, Long userId) {
        PaymentResponse<PgResponse.Find> result = pgFeignClient.findByPaymentId(paymentId, userId);
        return PgResult.Find.from(result.getData());
    }
}

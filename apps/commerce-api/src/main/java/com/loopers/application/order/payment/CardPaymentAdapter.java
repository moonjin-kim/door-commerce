package com.loopers.application.order.payment;

import com.loopers.domain.pg.PgService;
import com.loopers.infrastructure.pg.PgRequest;
import com.loopers.domain.payment.PaymentInfo;
import com.loopers.domain.payment.PaymentService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeoutException;

@Slf4j
@Component("CARD")
@RequiredArgsConstructor
public class CardPaymentAdapter implements PaymentMethod {
    private final PgService pgService;
    private final PaymentService paymentService;

    @Override
    public PaymentInfo.Pay pay(PaymentCriteria.Pay criteria) {
        String callbackUrl = "http://localhost:8080/api/v1/orders/callback";

        PaymentInfo.Pay paymentResult = paymentService.pay(
            criteria.toCommand()
        );


        // todo: 리뷰 포인트
        try {
            pgService.payment(
                    PgRequest.Pay.from(
                            criteria,
                            callbackUrl
                    ),
                    criteria.userId()
            );
        } catch (FeignException.BadRequest e) {
            log.error("PG 결제가 실패하였습니다. orderId: {}", criteria.orderId(), e);
            paymentResult = paymentService.paymentFail(criteria.orderId(), e.getMessage());
        } catch (Exception e) {
            log.error("PG 결제 중 예외가 발생하였습니다. orderId: {}", criteria.orderId(), e);
        }

        return paymentResult;
    }
}

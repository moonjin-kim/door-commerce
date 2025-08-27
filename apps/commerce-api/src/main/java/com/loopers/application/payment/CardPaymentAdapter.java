package com.loopers.application.payment;

import com.loopers.domain.pg.PgService;
import com.loopers.infrastructure.comman.CommonApplicationPublisher;
import com.loopers.infrastructure.pg.PgRequest;
import com.loopers.domain.payment.PaymentInfo;
import com.loopers.domain.payment.PaymentService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("CARD")
@RequiredArgsConstructor
public class CardPaymentAdapter implements PaymentMethod {
    private final PgService pgService;
    private final PaymentService paymentService;
    private final CommonApplicationPublisher eventPublisher;

    @Override
    public PaymentInfo.Pay pay(PaymentCriteria.RequestPayment criteria) {
        String callbackUrl = "http://localhost:8080/api/v1/payments/callback";

        PaymentInfo.Pay paymentResult = paymentService.requestPayment(
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
        } catch (CoreException e) {
            log.error("PG 결제가 실패하였습니다. orderId: {}", criteria.orderId(), e);
            if (e.getErrorType().equals(ErrorType.PAYMENT_DECLINED)) {
                paymentResult = paymentService.paymentFail(criteria.orderId(), e.getMessage());
                eventPublisher.publish(PaymentEvent.Failed.of(criteria.orderId()));
            }
        } catch (Exception e) {
            log.error("PG 결제 중 예외가 발생하였습니다. orderId: {}", criteria.orderId(), e);
        }

        return paymentResult;
    }
}

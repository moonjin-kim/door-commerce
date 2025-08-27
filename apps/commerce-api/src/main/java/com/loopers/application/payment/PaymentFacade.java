package com.loopers.application.payment;

import com.loopers.application.order.OrderCriteria;
import com.loopers.domain.PgInfo;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.pg.PgService;
import com.loopers.infrastructure.comman.CommonApplicationPublisher;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class PaymentFacade {
    private final PaymentService paymentService;
    private final PgService pgService;
    private final CommonApplicationPublisher eventPublisher;
    private final PaymentProcessor paymentProcessor;

    public void requestPayment(PaymentCriteria.RequestPayment criteria) {
        paymentProcessor.processPayment(criteria);
    }

    @Transactional
    public void callback(PaymentCriteria.Callback criteria) {
        // 주문 조회
        Payment payment = paymentService.getPaymentByOrderId(criteria.orderId()).orElseThrow(
                () -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 결제: " + criteria.orderId() )
        );

        PgInfo.Find pgResult = pgService.findByTransactionKey(criteria.transactionKey(), payment.getUserId());
        if(pgResult == null) {
            eventPublisher.publish(PaymentEvent.Failed.of(criteria.orderId()));
        }else if(!Objects.equals(pgResult.status(), "SUCCESS")) {
            eventPublisher.publish(PaymentEvent.Failed.of(criteria.orderId()));
        }


        // 결제 완료 저장
        paymentService.paymentComplete(payment.getOrderId(), pgResult.transactionKey());
        eventPublisher.publish(PaymentEvent.Success.of(criteria.orderId()));
    }
}

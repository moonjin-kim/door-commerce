package com.loopers.application.payment;

import com.github.f4b6a3.uuid.UuidCreator;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentInfo;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointService;
import com.loopers.infrastructure.comman.CommonApplicationPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointPaymentAdapter implements PaymentMethod {
    private final PointService pointService;
    private final PaymentService paymentService;

    @Override
    public PaymentMethodType getMethodType() {
        return PaymentMethodType.POINT;
    }

    @Transactional
    @Override
    public PaymentInfo.Pay pay(PaymentCriteria.RequestPayment criteria) {
        paymentService.createPayment(PaymentCommand.Pay.of(
                criteria.orderId(),
                criteria.userId(),
                criteria.amount(),
                criteria.method()
        ));

        try {
            pointService.using(PointCommand.Using.of(
                    criteria.userId(),
                    criteria.orderId(),
                    criteria.amount()
            ));
            String transactionKey = UuidCreator.getTimeOrdered().toString();
            return paymentService.paymentComplete(criteria.orderId(), transactionKey);

        } catch (Exception e) {
            log.error("포인트 결제가 실패하였습니다. orderId: {}", criteria.orderId(), e);
            return paymentService.paymentFail(criteria.orderId(), e.getMessage());

        }
    }
}

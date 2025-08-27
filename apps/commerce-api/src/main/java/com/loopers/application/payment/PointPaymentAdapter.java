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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("POINT")
@RequiredArgsConstructor
public class PointPaymentAdapter implements PaymentMethod {
    private final PointService pointService;
    private final PaymentService paymentService;
    private final CommonApplicationPublisher eventPublisher;

    @Override
    @Transactional
    public PaymentInfo.Pay pay(PaymentCriteria.RequestPayment criteria) {
        PaymentInfo.Pay payInfo = paymentService.requestPayment(PaymentCommand.Pay.of(
                criteria.orderId(),
                criteria.userId(),
                criteria.amount(),
                criteria.method()
        ));

        try {
            Point point = pointService.using(PointCommand.Using.of(
                    criteria.userId(),
                    criteria.orderId(),
                    criteria.amount()
            ));
            String transactionKey = UuidCreator.getTimeOrdered().toString();
            payInfo = paymentService.paymentComplete(criteria.orderId(), transactionKey);
            eventPublisher.publish(PaymentEvent.Success.of(criteria.orderId()));
        } catch (Exception e) {
            payInfo = paymentService.paymentFail(criteria.orderId(), e.getMessage());
            eventPublisher.publish(PaymentEvent.Failed.of(criteria.orderId()));
        }

        return payInfo;
    }
}

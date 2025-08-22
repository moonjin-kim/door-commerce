package com.loopers.application.order.payment;

import com.github.f4b6a3.uuid.UuidCreator;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentInfo;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.payment.PaymentType;
import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("POINT")
@RequiredArgsConstructor
public class PointPaymentAdapter implements PaymentMethod {
    private final PointService pointService;
    private final PaymentService paymentService;

    @Override
    public PaymentInfo.Pay pay(PaymentCriteria.Pay criteria) {
        PaymentInfo.Pay payInfo = paymentService.pay(PaymentCommand.Pay.of(
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
        } catch (Exception e) {
            payInfo = paymentService.paymentFail(criteria.orderId(), e.getMessage());
        }

        return payInfo;
    }
}

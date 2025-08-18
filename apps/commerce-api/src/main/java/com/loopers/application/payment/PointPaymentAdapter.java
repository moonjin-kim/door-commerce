package com.loopers.application.payment;

import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentInfo;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("POINT")
@RequiredArgsConstructor
public class PointPaymentAdapter implements PaymentMethod {
    private final PointService pointService;
    private final PaymentService paymentService;

    @Override
    public PaymentInfo.Pay pay(PaymentCriteria.Pay criteria) {
        PointCommand.Using pointUsingCommand = PointCommand.Using.of(
                criteria.userId(),
                criteria.orderId(),
                criteria.amount()
        );
        Point point = pointService.using(pointUsingCommand);

        paymentService.pay(PaymentCommand.Pay.of(
                criteria.orderId(),
                criteria.userId(),
                criteria.amount(),
                PaymentMethodType.POINT.name()
        ));
        return paymentService.paymentComplete(criteria.orderId());
    }
}

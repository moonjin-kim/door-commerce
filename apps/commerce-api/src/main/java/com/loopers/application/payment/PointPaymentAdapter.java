package com.loopers.application.payment;

import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentInfo;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.payment.PaymentType;
import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointInfo;
import com.loopers.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component("pointPayment")
@RequiredArgsConstructor
public class PointPaymentAdapter implements PaymentMethod {
    private final PointService pointService;
    private final PaymentService paymentService;

    @Override
    public PaymentInfo.Pay pay(PaymentCommand.Pay command) {
        PointCommand.Using pointUsingCommand = PointCommand.Using.of(
                command.userId(),
                command.orderId(),
                command.amount()
        );
        PointInfo pointInfo = pointService.using(pointUsingCommand);

        return paymentService.pay(command);
    }
}

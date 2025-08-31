package com.loopers.interfaces.event.payment;

import com.loopers.application.payment.PaymentCriteria;
import com.loopers.application.payment.PaymentFacade;
import com.loopers.domain.order.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PaymentListener {
    private final PaymentFacade paymentFacade;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handle(OrderEvent.CreateComplete event) {
        paymentFacade.requestPayment(PaymentCriteria.RequestPayment.from(
                event
        ));
    }
}

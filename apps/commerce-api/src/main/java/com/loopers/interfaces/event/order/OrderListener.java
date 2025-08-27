package com.loopers.interfaces.event.order;

import com.loopers.application.order.OrderFacade;
import com.loopers.domain.payment.PaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderListener {
    private final OrderFacade orderFacade;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    void handle(PaymentEvent.Failed event) {
        orderFacade.cancelOrder(event.orderId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    void handle(PaymentEvent.Success event) {
        orderFacade.completeOrder(event.orderId());
    }

}

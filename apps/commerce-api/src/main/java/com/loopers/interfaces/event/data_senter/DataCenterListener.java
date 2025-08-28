package com.loopers.interfaces.event.data_senter;

import com.loopers.domain.order.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class DataCenterListener {
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handle(OrderEvent.Complete event) {
        log.info("Order complete event: {}", event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handle(OrderEvent.Cancel event) {
        log.info("Order Cancel event: {}", event);
    }
}

package com.loopers.interfaces.event.order;

import com.loopers.application.order.OrderFacade;
import com.loopers.application.payment.PaymentCriteria;
import com.loopers.config.kafka.KafkaConfig;
import com.loopers.domain.order.OrderEvent;
import com.loopers.domain.payment.PaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderListener {
    private final OrderFacade orderFacade;

    @KafkaListener(topics = "payment.success", groupId = "order-listener", containerFactory = KafkaConfig.BATCH_LISTENER)
    void handle(List<PaymentEvent.Success> events) {
        for (PaymentEvent.Success event : events) {
            orderFacade.completeOrder(event.orderId());
        }
    }

    @KafkaListener(topics = "payment.failed", groupId = "order-listener", containerFactory = KafkaConfig.BATCH_LISTENER)
    void handleFailedEvent(List<PaymentEvent.Failed> events) {
        for (PaymentEvent.Failed event : events) {
            orderFacade.cancelOrder(event.orderId());
        }
    }
}

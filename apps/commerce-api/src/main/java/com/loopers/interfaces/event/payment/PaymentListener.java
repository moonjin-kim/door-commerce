package com.loopers.interfaces.event.payment;

import com.loopers.application.payment.PaymentCriteria;
import com.loopers.application.payment.PaymentFacade;
import com.loopers.config.kafka.KafkaConfig;
import com.loopers.domain.order.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentListener {
    private final PaymentFacade paymentFacade;

    @KafkaListener(topics = "order.create-complete", groupId = "payment-listener", containerFactory = KafkaConfig.BATCH_LISTENER)
    void handle(List<OrderEvent.CreateComplete> events) {
        for (OrderEvent.CreateComplete event : events) {
            paymentFacade.requestPayment(PaymentCriteria.RequestPayment.from(event));
        }
    }
}

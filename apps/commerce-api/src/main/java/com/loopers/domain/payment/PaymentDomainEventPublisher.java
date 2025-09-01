package com.loopers.domain.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentDomainEventPublisher implements PaymentEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Override
    public void publish(PaymentEvent.Success event) {
        kafkaTemplate.send("payment.success", event.orderId(), event);
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publish(PaymentEvent.Failed event) {
        kafkaTemplate.send("payment.fail", event.orderId(), event);
        applicationEventPublisher.publishEvent(event);
    }
}

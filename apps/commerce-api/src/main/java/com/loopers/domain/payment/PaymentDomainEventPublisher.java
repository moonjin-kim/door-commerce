package com.loopers.domain.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentDomainEventPublisher implements PaymentEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(PaymentEvent.Success event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publish(PaymentEvent.Failed event) {
        applicationEventPublisher.publishEvent(event);
    }
}

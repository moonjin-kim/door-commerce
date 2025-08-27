package com.loopers.domain.payment;

public interface PaymentEventPublisher {
    void publish(PaymentEvent.Success event);
    void publish(PaymentEvent.Failed event);
}

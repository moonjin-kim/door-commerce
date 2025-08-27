package com.loopers.application.payment;

public interface PaymentEventPublisher {
    void publish(PaymentEvent.Success event);
    void publish(PaymentEvent.Failed event);
}

package com.loopers.application.order;

import com.loopers.domain.order.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderApplicationEventPublisher implements OrderEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(OrderEvent.RequestPayment event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publish(OrderEvent.Complete event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publish(OrderEvent.Cancel event) {
        applicationEventPublisher.publishEvent(event);
    }
}

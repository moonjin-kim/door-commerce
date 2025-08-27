package com.loopers.application.order;

import com.loopers.infrastructure.comman.CommonApplicationPublisher;
import com.loopers.infrastructure.order.OrderEvent;
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
}

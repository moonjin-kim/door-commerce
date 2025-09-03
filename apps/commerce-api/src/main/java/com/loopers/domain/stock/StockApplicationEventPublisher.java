package com.loopers.domain.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockApplicationEventPublisher implements StockEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(StockEvent.Consumed event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publish(StockEvent.Out event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publish(StockEvent.Rollback event) {
        applicationEventPublisher.publishEvent(event);
    }
}

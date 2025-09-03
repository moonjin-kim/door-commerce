package com.loopers.domain.stock;

public interface StockEventPublisher {
    void publish(StockEvent.Consumed event);
    void publish(StockEvent.Out event);
    void publish(StockEvent.Rollback event);
}

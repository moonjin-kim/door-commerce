package com.loopers.application.order;

import com.loopers.domain.order.OrderEvent;

public interface OrderEventPublisher {
    void publish(OrderEvent.RequestPayment event);
    void publish(OrderEvent.ConsumeStockCommand event);
    void publish(OrderEvent.RollbackStockCommand event);
    void publish(OrderEvent.Complete event);
    void publish(OrderEvent.Cancel event);
}

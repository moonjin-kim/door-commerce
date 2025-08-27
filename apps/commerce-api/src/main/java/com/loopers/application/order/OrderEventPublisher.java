package com.loopers.application.order;

import com.loopers.infrastructure.order.OrderEvent;

public interface OrderEventPublisher {
    void publish(OrderEvent.RequestPayment event);
}

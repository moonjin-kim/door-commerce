package com.loopers.application.order;

import com.loopers.domain.order.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderKafkaEventPublisher implements OrderEventPublisher{
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Override
    public void publish(OrderEvent.CreateComplete event) {
        kafkaTemplate.send(
                "order.create-complete",event.orderId(), event
        );
    }

    @Override
    public void publish(OrderEvent.ConsumeStockCommand event) {
        kafkaTemplate.send("order.consume-stock",event.productId(), event);
    }

    @Override
    public void publish(OrderEvent.RollbackStockCommand event) {
        kafkaTemplate.send("order.rollback-stock",event.productId(), event);
    }

    @Override
    public void publish(OrderEvent.Complete event) {
        kafkaTemplate.send("order.complete",event.orderId(), event);
    }

    @Override
    public void publish(OrderEvent.Cancel event) {
        kafkaTemplate.send("order.cancel",event.orderId(), event);
    }
}

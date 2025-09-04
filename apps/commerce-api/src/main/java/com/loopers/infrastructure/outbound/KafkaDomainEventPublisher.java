package com.loopers.infrastructure.outbound;

import com.loopers.application.outbound.OutboundEventPublisher;
import com.loopers.application.product.ProductEvent;
import com.loopers.domain.like.LikeEvent;
import com.loopers.domain.stock.StockEvent;
import com.loopers.support.kafka.KafkaMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class KafkaDomainEventPublisher implements OutboundEventPublisher {
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Override
    public void publish(LikeEvent.Like event) {
        System.out.println("Publishing LikeEvent.Like event to Kafka for productId: " + event.productId() + ", userId: " + event.userId());
        KafkaMessage<LikeMessage.V1.Changed> message = KafkaMessage.of(
                UUID.randomUUID().toString(),
                LikeMessage.V1.VERSION,
                LocalDateTime.now(),
                LikeMessage.V1.Type.CHANGED,
                LikeMessage.V1.Changed.like(event.productId(), event.userId())
        );
        kafkaTemplate.send(
                LikeMessage.TOPIC.LIKE,
                String.valueOf(event.productId()),
                message
        );
    }

    @Override
    public void publish(LikeEvent.UnLike event) {

        KafkaMessage<LikeMessage.V1.Changed> message = KafkaMessage.of(
                UUID.randomUUID().toString(),
                LikeMessage.V1.VERSION,
                LocalDateTime.now(),
                LikeMessage.V1.Type.CHANGED,
                LikeMessage.V1.Changed.unlike(event.productId(), event.userId())
        );
        kafkaTemplate.send(
                LikeMessage.TOPIC.LIKE,
                String.valueOf(event.productId()),
                message
        );
    }

    @Override
    public void publish(StockEvent.Consumed event) {
        KafkaMessage<StockMessage.V1.Changed> message = KafkaMessage.of(
                UUID.randomUUID().toString(),
                StockMessage.V1.VERSION,
                LocalDateTime.now(),
                StockMessage.V1.Type.CHANGE,
                StockMessage.V1.Changed.sale(event.productId(), event.quantity())
        );
        kafkaTemplate.send(
                StockMessage.TOPIC,
                String.valueOf(event.productId()),
                message
        );
    }

    @Override
    public void publish(StockEvent.Rollback event) {
        KafkaMessage<StockMessage.V1.Changed> message = KafkaMessage.of(
                UUID.randomUUID().toString(),
                StockMessage.V1.VERSION,
                LocalDateTime.now(),
                StockMessage.V1.Type.CHANGE,
                StockMessage.V1.Changed.cancel(event.productId(), event.quantity())
        );
        kafkaTemplate.send(
                StockMessage.TOPIC,
                String.valueOf(event.productId()),
                message
        );
    }

    @Override
    public void publish(ProductEvent.View event) {
        KafkaMessage<StockMessage.V1.SoldOut> message = KafkaMessage.of(
                UUID.randomUUID().toString(),
                StockMessage.V1.VERSION,
                LocalDateTime.now(),
                StockMessage.V1.Type.SOLD_OUT,
                StockMessage.V1.SoldOut.of(event.productId())
        );
        kafkaTemplate.send(
                StockMessage.TOPIC,
                String.valueOf(event.productId()),
                message
        );
    }

    @Override
    public void publish(StockEvent.Out event) {
        KafkaMessage<StockMessage.V1.SoldOut> message = KafkaMessage.of(
                UUID.randomUUID().toString(),
                StockMessage.V1.VERSION,
                LocalDateTime.now(),
                StockMessage.V1.Type.SOLD_OUT,
                StockMessage.V1.SoldOut.of(event.productId())
        );
        kafkaTemplate.send(
                StockMessage.TOPIC,
                String.valueOf(event.productId()),
                message
        );
    }
}

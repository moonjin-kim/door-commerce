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
                StockMessage.TOPIC.CHANGE,
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
                StockMessage.TOPIC.CHANGE,
                String.valueOf(event.productId()),
                message
        );
    }

    @Override
    public void publish(ProductEvent.View event) {
        KafkaMessage<StockMessage.V1.Out> message = KafkaMessage.of(
                UUID.randomUUID().toString(),
                StockMessage.V1.VERSION,
                LocalDateTime.now(),
                StockMessage.V1.Type.OUT,
                StockMessage.V1.Out.of(event.productId())
        );
        kafkaTemplate.send(
                StockMessage.TOPIC.CHANGE,
                String.valueOf(event.productId()),
                message
        );
    }

    @Override
    public void publish(StockEvent.Out event) {
        KafkaMessage<ProductMessage.V1.Viewed> message = KafkaMessage.of(
                UUID.randomUUID().toString(),
                ProductMessage.V1.VERSION,
                LocalDateTime.now(),
                ProductMessage.V1.Type.VIEW,
                ProductMessage.V1.Viewed.of(event.productId())
        );
        kafkaTemplate.send(
                ProductMessage.TOPIC.VIEW,
                String.valueOf(event.productId()),
                message
        );
    }
}

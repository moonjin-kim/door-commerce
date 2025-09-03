package com.loopers.interfaces.event.kafka;

import com.loopers.application.product.ProductEvent;
import com.loopers.domain.like.LikeEvent;
import com.loopers.domain.stock.StockEvent;
import com.loopers.support.kafka.KafkaMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(LikeEvent.Like event) {
        KafkaMessage<LikeMessage.V1.Changed> message = KafkaMessage.of(
                UUID.randomUUID().toString(),
                LikeMessage.V1.VERSION,
                LocalDateTime.now(),
                LikeMessage.V1.Type.LIKE,
                LikeMessage.V1.Changed.like(event.productId(), event.userId())
        );
        kafkaTemplate.send(
                LikeMessage.V1.TOPIC.LIKE,
                String.valueOf(event.productId()),
                message
        );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(LikeEvent.UnLike event) {
        KafkaMessage<LikeMessage.V1.Changed> message = KafkaMessage.of(
                UUID.randomUUID().toString(),
                LikeMessage.V1.VERSION,
                LocalDateTime.now(),
                LikeMessage.V1.Type.LIKE,
                LikeMessage.V1.Changed.unlike(event.productId(), event.userId())
        );
        kafkaTemplate.send(
                LikeMessage.V1.TOPIC.LIKE,
                String.valueOf(event.productId()),
                message
        );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(StockEvent.Consumed event) {
        KafkaMessage<StockMessage.V1.Changed> message = KafkaMessage.of(
                UUID.randomUUID().toString(),
                StockMessage.V1.VERSION,
                LocalDateTime.now(),
                StockMessage.V1.Type.CHANGE,
                StockMessage.V1.Changed.sale(event.productId(), event.quantity())
        );
        kafkaTemplate.send(
                StockMessage.V1.TOPIC.CHANGE,
                String.valueOf(event.productId()),
                message
        );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(StockEvent.Rollback event) {
        KafkaMessage<StockMessage.V1.Changed> message = KafkaMessage.of(
                UUID.randomUUID().toString(),
                StockMessage.V1.VERSION,
                LocalDateTime.now(),
                StockMessage.V1.Type.CHANGE,
                StockMessage.V1.Changed.cancel(event.productId(), event.quantity())
        );
        kafkaTemplate.send(
                StockMessage.V1.TOPIC.CHANGE,
                String.valueOf(event.productId()),
                message
        );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(StockEvent.Out event) {
        KafkaMessage<StockMessage.V1.Out> message = KafkaMessage.of(
                UUID.randomUUID().toString(),
                StockMessage.V1.VERSION,
                LocalDateTime.now(),
                StockMessage.V1.Type.OUT,
                StockMessage.V1.Out.of(event.productId())
        );
        kafkaTemplate.send(
                StockMessage.V1.TOPIC.CHANGE,
                String.valueOf(event.productId()),
                message
        );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ProductEvent.View event) {
        KafkaMessage<ProductMessage.V1.Viewed> message = KafkaMessage.of(
                UUID.randomUUID().toString(),
                ProductMessage.V1.VERSION,
                LocalDateTime.now(),
                ProductMessage.V1.Type.VIEW,
                ProductMessage.V1.Viewed.of(event.productId())
        );
        kafkaTemplate.send(
                ProductMessage.V1.TOPIC.VIEW,
                String.valueOf(event.productId()),
                message
        );
    }
}

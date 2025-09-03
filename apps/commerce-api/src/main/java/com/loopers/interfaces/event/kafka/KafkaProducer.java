package com.loopers.interfaces.event.kafka;

import com.loopers.domain.like.LikeEvent;
import com.loopers.domain.order.OrderEvent;
import com.loopers.domain.product.ProductEvent;
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
    void handle(OrderEvent.Complete event) {
        KafkaMessage<OrderEvent.Complete> message = KafkaMessage.of(
                UUID.randomUUID().toString(),
                "v1",
                LocalDateTime.now(),
                "ORDER_COMPLETE",
                event
        );
        kafkaTemplate.send(
                "like.likeChange",
                event.orderId(),
                message
        );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handle(ProductEvent.Inquiry event) {
        KafkaMessage<ProductEvent.Inquiry> message = KafkaMessage.of(
                UUID.randomUUID().toString(),
                "v1",
                LocalDateTime.now(),
                "INQUIRY",
                event
        );
        kafkaTemplate.send(
                "product.stockChange",
                event.productId(),
                message
        );
    }
}

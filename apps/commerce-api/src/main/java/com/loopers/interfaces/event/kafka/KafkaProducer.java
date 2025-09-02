package com.loopers.interfaces.event.kafka;

import com.loopers.domain.like.LikeEvent;
import com.loopers.domain.order.OrderEvent;
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
        KafkaMessage<LikeEvent.Like> message = KafkaMessage.of(
                UUID.randomUUID().toString(),
                "v1",
                LocalDateTime.now(),
                "Like",
                event
        );
        kafkaTemplate.send(
                "like.likeChange",
                event.productId(),
                message
        );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(LikeEvent.UnLike event) {
        KafkaMessage<LikeEvent.UnLike> message = KafkaMessage.of(
                UUID.randomUUID().toString(),
                "v1",
                LocalDateTime.now(),
                "UnLike",
                event
        );
        kafkaTemplate.send(
                "like.likeChange",
                event.productId(),
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
                "UnLike",
                event
        );
        kafkaTemplate.send(
                "like.likeChange",
                event.orderId(),
                message
        );
    }
}

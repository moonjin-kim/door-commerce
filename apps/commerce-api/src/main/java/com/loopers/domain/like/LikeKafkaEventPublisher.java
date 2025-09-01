package com.loopers.domain.like;

import com.loopers.domain.payment.PaymentEvent;
import com.loopers.domain.payment.PaymentEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeKafkaEventPublisher implements LikeEventPublisher {
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Override
    public void publish(LikeEvent.Like event) {
        kafkaTemplate.send("like.like", event);
    }

    @Override
    public void publish(LikeEvent.UnLike event) {
        kafkaTemplate.send("like.unlike", event);
    }
}

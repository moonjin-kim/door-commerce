package com.loopers.interfaces.event.data_senter;

import com.loopers.config.kafka.KafkaConfig;
import com.loopers.domain.like.LikeEvent;
import com.loopers.domain.order.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@Slf4j
public class DataCenterListener {
    @KafkaListener(topics = "order.cancel", groupId = "data-center", containerFactory = KafkaConfig.BATCH_LISTENER)
    void handleCancelList(List<OrderEvent.Cancel> events) {
        for (OrderEvent.Cancel event : events) {
            log.info("Order cancelled: {}", event);
        }
    }

    @KafkaListener(topics = "order.complete", groupId = "data-center", containerFactory = KafkaConfig.BATCH_LISTENER)
    void handleCompleteList(List<OrderEvent.Complete> events) {
        for (OrderEvent.Complete event : events) {
            log.info("Order completed: {}", event);
        }
    }

    @KafkaListener(topics = "order.create-complete", groupId = "data-center", containerFactory = KafkaConfig.BATCH_LISTENER)
    void handleCreateCompleteList(List<OrderEvent.CreateComplete> events) {
        for (OrderEvent.CreateComplete event : events) {
            log.info("결제 요청 {}", event);
        }
    }

    @KafkaListener(topics = "like.like", groupId = "data-center", containerFactory = KafkaConfig.BATCH_LISTENER)
    void handleLikeList(List<LikeEvent.Like> events) {
        for (LikeEvent.Like event : events) {
            log.info("유저의 좋아요: {}", event);
        }
    }

    @KafkaListener(topics = "like.unlike", groupId = "data-center", containerFactory = KafkaConfig.BATCH_LISTENER)
    void handleUnLikeList(List<LikeEvent.UnLike> events) {
        for (LikeEvent.UnLike event : events) {
            log.info("유저의 좋아요 취소: {}", event);
        }
    }

}

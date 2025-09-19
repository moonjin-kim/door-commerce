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
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleCancelList(OrderEvent.Cancel event) {
        log.info("Order cancelled: {}", event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleCompleteList(OrderEvent.Complete event) {
        log.info("Order completed: {}", event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleCreateCompleteList(OrderEvent.CreateComplete event) {
        log.info("결제 요청 {}", event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleLikeList(LikeEvent.Like event) {
        log.info("유저의 좋아요: {}", event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleUnLikeList(LikeEvent.UnLike event) {
        log.info("유저의 좋아요 취소: {}", event);
    }

}

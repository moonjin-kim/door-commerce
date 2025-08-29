package com.loopers.interfaces.event.data_senter;

import com.loopers.domain.like.LikeEvent;
import com.loopers.domain.order.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class DataCenterListener {
    @Async
    @EventListener
    void handle(OrderEvent.Complete event) {
        log.info("Order complete event: {}", event);
    }

    @Async
    @EventListener
    void handle(OrderEvent.Cancel event) {
        log.info("Order Cancel event: {}", event);
    }

    @Async
    @EventListener
    void handle(OrderEvent.RequestPayment event) {
        log.info("결제 요청 {}", event);
    }

    @Async
    @EventListener
    void handle(LikeEvent.Like event) {
        log.info("유저의 좋아요: {}", event);
    }

    @Async
    @EventListener
    void handle(LikeEvent.UnLike event) {
        log.info("유저의 좋아요 취소: {}", event);
    }

}

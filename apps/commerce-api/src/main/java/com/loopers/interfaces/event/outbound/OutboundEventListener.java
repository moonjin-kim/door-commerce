package com.loopers.interfaces.event.outbound;

import com.loopers.application.outbound.OutboundEventPublisher;
import com.loopers.application.product.ProductEvent;
import com.loopers.domain.like.LikeEvent;
import com.loopers.domain.stock.StockEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OutboundEventListener {
    private final OutboundEventPublisher outboundEventPublisher;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(LikeEvent.Like event) {
        outboundEventPublisher.publish(event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(LikeEvent.UnLike event) {
        outboundEventPublisher.publish(event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(StockEvent.Consumed event) {
        outboundEventPublisher.publish(event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(StockEvent.Rollback event) {
        outboundEventPublisher.publish(event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(StockEvent.Out event) {
        outboundEventPublisher.publish(event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ProductEvent.View event) {
        outboundEventPublisher.publish(event);
    }
}

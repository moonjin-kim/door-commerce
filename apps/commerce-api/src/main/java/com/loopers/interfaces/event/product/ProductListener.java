package com.loopers.interfaces.event.product;

import com.loopers.application.product.ProductFacade;
import com.loopers.domain.like.LikeEvent;
import com.loopers.domain.stock.StockEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductListener {
    private final ProductFacade productFacade;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLike(LikeEvent.Like event) {
        productFacade.increaseLikeCount(event.productId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUnlike(LikeEvent.UnLike event) {
        productFacade.decreaseLikeCount(event.productId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(StockEvent.Out event) {
        productFacade.soldOut(event.productId());
    }
}

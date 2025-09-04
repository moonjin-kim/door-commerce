package com.loopers.application.outbound;

import com.loopers.application.product.ProductEvent;
import com.loopers.domain.like.LikeEvent;
import com.loopers.domain.stock.StockEvent;

public interface OutboundEventPublisher {
    void publish(LikeEvent.Like event);
    void publish(LikeEvent.UnLike event);
    void publish(StockEvent.Consumed event);
    void publish(StockEvent.Rollback event);
    void publish(ProductEvent.View event);
    void publish(StockEvent.Out event);
}

package com.loopers.interfaces.event.product;

import com.loopers.config.kafka.KafkaConfig;
import com.loopers.domain.like.LikeEvent;
import com.loopers.domain.order.OrderEvent;
import com.loopers.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ProductListener {
    private final ProductService productService;

    @KafkaListener(topics = "like.like", groupId = "product-listener", containerFactory = KafkaConfig.BATCH_LISTENER)
    public void handleConsumeStock(List<LikeEvent.Like> events) {
        for (LikeEvent.Like event : events) {
            productService.increaseLikeCount(event.productId());
        }
    }

    @KafkaListener(topics = "like.unlike", groupId = "product-listener", containerFactory = KafkaConfig.BATCH_LISTENER)
    public void handleUnlike(List<LikeEvent.UnLike> events) {
        for (LikeEvent.UnLike event : events) {
            productService.decreaseLikeCount(event.productId());
        }
    }
}

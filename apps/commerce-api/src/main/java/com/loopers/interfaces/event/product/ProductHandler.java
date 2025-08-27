package com.loopers.interfaces.event.product;

import com.loopers.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ProductHandler {
    private final ProductService productService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    void handle(ProductEvent.IncreaseLikeCount event) {
        productService.increaseLikeCount(event.productId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    void handle(ProductEvent.DecreaseLikeCount event) {
        productService.decreaseLikeCount(event.productId());
    }
}

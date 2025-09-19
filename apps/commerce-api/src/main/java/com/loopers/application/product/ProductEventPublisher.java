package com.loopers.application.product;

public interface ProductEventPublisher {
    void handle(ProductEvent.View event);
}

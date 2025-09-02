package com.loopers.application.product;

import com.loopers.domain.product.ProductEvent;

public interface ProductEventPublisher {
    void handle(ProductEvent.Inquiry event);
}

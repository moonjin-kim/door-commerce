package com.loopers.application.product;

import com.loopers.domain.product.ProductEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductApplicationEventPublisher implements ProductEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void handle(ProductEvent.Inquiry event) {
        applicationEventPublisher.publishEvent(event);
    }
}

package com.loopers.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProductMetricService {
    private final ProductMetricRepository productMetricRepository;

    public ProductMetric save(ProductMetric productMetric) {
        return productMetricRepository.save(productMetric);
    }

    @Transactional
    public void updateLikeCount(ProductMetricCommand.LikeChange command) {
        ProductMetric productMetric = productMetricRepository.findBy(command.productId(), command.date())
                .orElseGet(() -> this.save(ProductMetric.create(command.productId(), command.date())));

        productMetric.updateLikeCount(command.delta());
    }

    @Transactional
    public void updateOrderQuantity(ProductMetricCommand.StockChange likeChange) {
        ProductMetric productMetric = productMetricRepository.findBy(likeChange.productId(), likeChange.date())
                .orElseGet(() -> this.save(ProductMetric.create(likeChange.productId(), likeChange.date())));

        productMetric.updateOrderQuantity(likeChange.quantity());
    }

    @Transactional
    public void updateViewCount(ProductMetricCommand.ViewChange command) {
        ProductMetric productMetric = productMetricRepository.findBy(command.productId(), command.date())
                .orElseGet(() -> this.save(ProductMetric.create(command.productId(), command.date())));

        productMetric.plusViewCount();
    }
}

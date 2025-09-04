package com.loopers.infrastructure.product;

import com.loopers.domain.product.ProductMetric;
import com.loopers.domain.product.ProductMetricRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductMetricRepositoryImpl implements ProductMetricRepository {
    private final ProductMetricJpaRepository productMetricJpaRepository;
    @Override
    public ProductMetric save(ProductMetric productMetric) {
        return productMetricJpaRepository.save(productMetric);
    }

    @Override
    public Optional<ProductMetric> findBy(Long productId, LocalDate date) {
        return productMetricJpaRepository.findByProductIdAndBucket(productId, date);
    }
}

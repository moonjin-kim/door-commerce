package com.loopers.domain;

import java.time.LocalDate;
import java.util.Optional;

public interface ProductMetricRepository {
    ProductMetric save(ProductMetric productMetric);
    Optional<ProductMetric> findBy(Long productId, LocalDate date);
}

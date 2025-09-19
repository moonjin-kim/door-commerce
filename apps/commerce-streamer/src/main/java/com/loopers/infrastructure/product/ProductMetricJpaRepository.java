package com.loopers.infrastructure.product;

import com.loopers.domain.product.ProductMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ProductMetricJpaRepository extends JpaRepository<ProductMetric, Long> {
    Optional<ProductMetric> findByProductIdAndBucket(Long productId, LocalDate bucket);
}

package com.loopers.infrastructure;

import com.loopers.domain.ProductMetric;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ProductMetricJpaRepository extends JpaRepository<ProductMetric, Long> {
    Page<ProductMetric> findByAggDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
}

package com.loopers.batch.step.weekly;

import com.loopers.domain.ProductMetric;
import com.loopers.infrastructure.ProductMetricJpaRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class ProductMetricsReader extends RepositoryItemReader<ProductMetric> {

    private final ProductMetricJpaRepository productMetricsJpaRepository;

    @Value("#{jobParameters['startDate']}")
    private String startDateParam;

    @Value("#{jobParameters['endDate']}")
    private String endDateParam;

    @PostConstruct
    public void initialize() {
        LocalDate startDate = LocalDate.parse(startDateParam);
        LocalDate endDate = LocalDate.parse(endDateParam);

        setRepository(productMetricsJpaRepository);
        setMethodName("findByAggDateBetween");

        setArguments(java.util.Arrays.asList(startDate, endDate));

        setPageSize(1000);

        // 정렬 설정
        Map<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("id", Sort.Direction.ASC);
        setSort(sorts);

        // 저장하지 않고 읽기만
        setSaveState(false);
    }
}

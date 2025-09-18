package com.loopers.domain.step;

import com.loopers.domain.ProductMetric;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

@Component
@StepScope
@RequiredArgsConstructor
public class ProductMetricReader implements ItemReader<ProductMetric> {
    private final EntityManagerFactory entityManagerFactory;

    // 내부적으로 상태를 가진 리더를 한번 생성해 위임
    private JpaPagingItemReader<ProductMetric> delegate;

    @Value("#{jobParameters['calcDate']}")
    private String calcDateParam;

    @Override
    public ProductMetric read() throws Exception {
        if (delegate == null) {
            LocalDate calcDate = LocalDate.parse(calcDateParam);
            LocalDate start = calcDate.minusDays(31);
            LocalDate end   = calcDate.minusDays(1);

            String jpql = """
                select pm
                from ProductMetric pm
                where pm.aggDate between :start and :end
                order by pm.productId asc, pm.aggDate asc
                """;

            delegate = new JpaPagingItemReaderBuilder<ProductMetric>()
                    .name("weeklyMetricJpaReader")
                    .entityManagerFactory(entityManagerFactory)
                    .pageSize(1000)
                    .queryString(jpql)
                    .parameterValues(Map.of("start", start, "end", end))
                    .build();
            delegate.afterPropertiesSet();
            delegate.open(new ExecutionContext());
        }

        ProductMetric row = delegate.read();
        if (row == null) delegate.close();
        return row;
    }

//    @Override
//    public WeeklyAggRow read() throws Exception {
//        if (delegate == null) {
//            LocalDate calcDate = (calcDateParam != null && !calcDateParam.isBlank())
//                    ? LocalDate.parse(calcDateParam)
//                    : LocalDate.now(ZoneId.of("Asia/Seoul"));
//            LocalDate start = calcDate.minusDays(30);  // today 포함 7일
//            LocalDate end   = calcDate;
//
//            String jpql = """
//                select new com.loopers.domain.ranking.WeeklyAggRow(
//                    pm.productId,
//                    sum(pm.likeCount),
//                    sum(pm.orderQuantity),
//                    sum(pm.viewCount)
//                )
//                from ProductMetric pm
//                where pm.aggDate between :start and :end
//                group by pm.productId
//                """;
//
//            delegate = new JpaPagingItemReaderBuilder<WeeklyAggRow>()
//                    .name("weeklyAggJpaReader")
//                    .entityManagerFactory(entityManagerFactory)
//                    .pageSize(1000)
//                    .queryString(jpql)
//                    .parameterValues(Map.of("start", start, "end", end))
//                    .build();
//            delegate.afterPropertiesSet();
//            delegate.open(new org.springframework.batch.item.ExecutionContext());
//        }
//        WeeklyAggRow row = delegate.read();
//        if (row == null) {
//            delegate.close();
//        }
//        return row;
//    }
}

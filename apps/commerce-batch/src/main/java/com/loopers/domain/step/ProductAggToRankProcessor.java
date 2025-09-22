package com.loopers.domain.step;

import com.loopers.domain.ProductMetric;
import com.loopers.domain.ranking.WeeklyRankRow;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@StepScope
@RequiredArgsConstructor
public class ProductAggToRankProcessor implements ItemProcessor<ProductMetric, WeeklyRankRow> {

    @Value("#{jobParameters['calcDate']}")
    private String calcDateParam;

    @Override
    public WeeklyRankRow process(ProductMetric agg) {
        LocalDate calcDate = LocalDate.parse(calcDateParam);

        // 가중치(예시)
        BigDecimal wLike  = new BigDecimal("0.20");
        BigDecimal wOrder = new BigDecimal("0.70");
        BigDecimal wView  = new BigDecimal("0.10");

        BigDecimal score = BigDecimal.valueOf(agg.getLikeCount()).multiply(wLike)
                .add(BigDecimal.valueOf(agg.getOrderQuantity()).multiply(wOrder))
                .add(BigDecimal.valueOf(agg.getViewCount()).multiply(wView));

        return new WeeklyRankRow(calcDate, agg.getProductId(), score);
    }
}

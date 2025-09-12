package com.loopers.applicaiton.product;

import com.loopers.domain.product.ProductMetric;
import com.loopers.domain.product.ProductMetricCommand;
import com.loopers.domain.product.ProductMetricService;
import com.loopers.domain.ranking.RankingCommand;
import com.loopers.domain.ranking.RankingService;
import com.loopers.interfaces.consumer.product.LikeMessage;
import com.loopers.interfaces.consumer.product.ProductMessage;
import com.loopers.interfaces.consumer.product.StockMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ofPattern;

@Component
@RequiredArgsConstructor
public class ProductMetricFacade {
    private final ProductMetricService productMetricService;
    private final RankingService rankingService;

    public void updateViewCounts(List<ProductMessage.V1.Viewed> likeMessages, LocalDate today) {
        Map<Long, Long> aggregatedLikes = likeMessages.stream()
                .collect(Collectors.groupingBy(
                        ProductMessage.V1.Viewed::productId,
                        Collectors.counting()
                ));

        List<ProductMetric> updatedMetrics = new ArrayList<>();

        for (Map.Entry<Long, Long> entry : aggregatedLikes.entrySet()) {
            ProductMetricCommand.ViewChange command = new ProductMetricCommand.ViewChange(entry.getKey(), today,entry.getValue());
            ProductMetric updatedMetric = productMetricService.updateViewCount(command);
            updatedMetrics.add(updatedMetric);
        }

        List<RankingCommand.UpdateProductScores> productScores = updatedMetrics.stream().map(RankingCommand.UpdateProductScores::from).toList();
        rankingService.updateProductScores(productScores, today);

    }

    public void updateLikeCounts(List<LikeMessage.V1.Changed> likeMessages, LocalDate today) {
        Map<Long, Long> aggregatedLikes = likeMessages.stream()
                .collect(Collectors.groupingBy(
                        LikeMessage.V1.Changed::productId,
                        Collectors.summingLong(LikeMessage.V1.Changed::delta)
                ));

        List<ProductMetric> updatedMetrics = new ArrayList<>();

        for (Map.Entry<Long, Long> entry : aggregatedLikes.entrySet()) {
            ProductMetricCommand.LikeChange command = new ProductMetricCommand.LikeChange(entry.getKey(), today, entry.getValue());
            ProductMetric updatedMetric = productMetricService.updateLikeCount(command);
            updatedMetrics.add(updatedMetric);
        }

        List<RankingCommand.UpdateProductScores> productScores = updatedMetrics.stream().map(RankingCommand.UpdateProductScores::from).toList();
        rankingService.updateProductScores(productScores, today);
    }

    public void updateOrderCounts(List<StockMessage.V1.Changed> likeMessages, LocalDate today) {
        Map<Long, Long> aggregatedLikes = likeMessages.stream()
                .collect(Collectors.groupingBy(
                        StockMessage.V1.Changed::productId,
                        Collectors.summingLong(StockMessage.V1.Changed::quantity)
                ));

        List<ProductMetric> updatedMetrics = new ArrayList<>();

        for (Map.Entry<Long, Long> entry : aggregatedLikes.entrySet()) {
            ProductMetricCommand.StockChange command = new ProductMetricCommand.StockChange(entry.getKey(), today, entry.getValue());
            ProductMetric updatedMetric = productMetricService.updateOrderQuantity(command);
            updatedMetrics.add(updatedMetric);
        }

        List<RankingCommand.UpdateProductScores> productScores = updatedMetrics.stream().map(RankingCommand.UpdateProductScores::from).toList();
        rankingService.updateProductScores(productScores, today);
    }
}

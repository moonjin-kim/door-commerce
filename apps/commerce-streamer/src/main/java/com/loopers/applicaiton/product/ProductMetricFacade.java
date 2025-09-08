package com.loopers.applicaiton.product;

import com.loopers.domain.event_hendler.EventHandlerService;
import com.loopers.domain.product.ProductMetric;
import com.loopers.domain.product.ProductMetricService;
import com.loopers.domain.ranking.RankingCommand;
import com.loopers.domain.ranking.RankingService;
import com.loopers.interfaces.consumer.product.LikeMessage;
import com.loopers.interfaces.consumer.product.ProductMessage;
import com.loopers.interfaces.consumer.product.StockMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ProductMetricFacade {
    private final ProductMetricService productMetricService;
    private final RankingService rankingService;

    public void updateLikeCount(LikeMessage.V1.Changed message, LocalDateTime publishedAt) {
        ProductMetric productMetric = productMetricService.updateLikeCount(message.toCommand(publishedAt.toLocalDate()));

        RankingCommand.UpdateProductScore command = RankingCommand.UpdateProductScore.from(
                productMetric,
                publishedAt.toLocalDate()
        );

        rankingService.updateProductScores(command);
    }

    public void updateOrderQuantity(StockMessage.V1.Changed message, LocalDateTime publishedAt) {
        ProductMetric productMetric = productMetricService.updateOrderQuantity(message.toCommand(publishedAt.toLocalDate()));

        RankingCommand.UpdateProductScore command = RankingCommand.UpdateProductScore.from(
                productMetric,
                publishedAt.toLocalDate()
        );

        rankingService.updateProductScores(command);
    }

    public void updateViewCount(ProductMessage.V1.Viewed message, LocalDateTime publishedAt) {
        ProductMetric productMetric = productMetricService.updateViewCount(message.toCommand(publishedAt.toLocalDate()));

        RankingCommand.UpdateProductScore command = RankingCommand.UpdateProductScore.from(
                productMetric,
                publishedAt.toLocalDate()
        );

        rankingService.updateProductScores(command);
    }
}

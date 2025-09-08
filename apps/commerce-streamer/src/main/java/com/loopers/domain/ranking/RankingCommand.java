package com.loopers.domain.ranking;

import com.loopers.domain.product.ProductMetric;

import java.time.LocalDate;

public class RankingCommand {
    public record UpdateProductScore(Long productId, Long likeCount, Long orderQuantity, Long viewCount, LocalDate date) {
        static public UpdateProductScore of(Long productId, Long likeCount, Long orderQuantity, Long viewCount, LocalDate date) {
            return new UpdateProductScore(productId, likeCount, orderQuantity, viewCount, date);
        }

        static public UpdateProductScore from(ProductMetric productMetric, LocalDate date) {
            return new UpdateProductScore(
                productMetric.getId(),
                productMetric.getLikeCount(),
                productMetric.getOrderQuantity(),
                productMetric.getViewCount(),
                date
            );
        }
    }
}

package com.loopers.domain.ranking;

import com.loopers.domain.product.ProductMetric;

import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ofPattern;

public class RankingCommand {
    public record UpdateProductScore(Long productId, Long likeCount, Long orderQuantity, Long viewCount, String date) {
        static public UpdateProductScore of(Long productId, Long likeCount, Long orderQuantity, Long viewCount, LocalDate date) {
            return new UpdateProductScore(productId, likeCount, orderQuantity, viewCount, date.format(ofPattern("yyyyMMdd")));
        }

        static public UpdateProductScore from(ProductMetric productMetric, LocalDate date) {
            return new UpdateProductScore(
                productMetric.getProductId(),
                productMetric.getLikeCount(),
                productMetric.getOrderQuantity(),
                productMetric.getViewCount(),
                date.format(ofPattern("yyyyMMdd"))
            );
        }
    }

    public record UpdateProductScores(Long productId, Long likeCount, Long orderQuantity, Long viewCount) {
        static public UpdateProductScores from(ProductMetric productMetric) {
            return new UpdateProductScores(productMetric.getProductId(), productMetric.getLikeCount(), productMetric.getOrderQuantity(), productMetric.getViewCount());
        }

        static public UpdateProductScores of(Long productId, Long likeCount, Long orderQuantity, Long viewCount) {
            return new UpdateProductScores(productId, likeCount, orderQuantity, viewCount);
        }
    }
}

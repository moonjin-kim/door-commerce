package com.loopers.interfaces.consumer.product;

import com.loopers.domain.product.ProductMetricCommand;

import java.time.LocalDate;

public class LikeMessage {
    public static final class TOPIC {
        public static final String CHANGED = "product.likeChange";
    }

    public static class V1 {
        public static final String VERSION = "V1";
        public static final class Type {
            public static final String CHANGED = "LIKE_CHANGED_V1";
        }

        public record Changed(Long productId, Long userId, Long delta) {
            public ProductMetricCommand.LikeChange toCommand(LocalDate date) {
                return new ProductMetricCommand.LikeChange(productId, date, delta);
            }
        }
    }
}

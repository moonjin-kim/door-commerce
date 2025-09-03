package com.loopers.interfaces.consumer.product;

import com.loopers.domain.product.ProductMetricCommand;

public class ProductMessage {
    public static class TOPIC {
        static final String VIEW = "product.view";
    }

    public static class V1 {
        public static final String VERSION = "v1";

        public static class Type {
            public static final String VIEW = "PRODUCT_VIEW";
        }

        public record Viewed(Long productId) {
            public ProductMetricCommand.ViewChange toCommand(java.time.LocalDate date) {
                return new ProductMetricCommand.ViewChange(productId, date);
            }
        }
    }
}

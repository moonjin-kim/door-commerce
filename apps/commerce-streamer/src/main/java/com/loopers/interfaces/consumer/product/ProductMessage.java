package com.loopers.interfaces.consumer.product;

import com.loopers.domain.product.ProductMetricCommand;

public class ProductMessage {
    public static final String TOPIC  = "product-event";

    public static class V1 {
        public static final String VERSION = "v1";

        public static class Type {
            public static final String VIEW = "PRODUCT_VIEW:V1";
        }

        public record Viewed(Long productId) {
            public ProductMetricCommand.ViewChange toCommand(java.time.LocalDate date) {
                return new ProductMetricCommand.ViewChange(productId, date);
            }
        }
    }
}

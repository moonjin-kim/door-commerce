package com.loopers.infrastructure.outbound;

public class ProductMessage {
    public static class TOPIC {
        public static final String VIEW = "product.view";
    }

    public static class V1 {
        public static final String VERSION = "v1";

        public static class Type {
            public static final String VIEW = "PRODUCT_VIEW:V1";
        }

        public record Viewed(Long productId) {
            public static Viewed of(Long productId) {
                return new Viewed(productId);
            }
        }
    }
}

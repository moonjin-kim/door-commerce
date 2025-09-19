package com.loopers.infrastructure.outbound;

public class ProductMessage {
    public static String TOPIC ="product-event";

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

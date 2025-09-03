package com.loopers.interfaces.event.kafka;

public class ProductMessage {
    static class V1 {
        static final String VERSION = "v1";

        static class TOPIC {
            static final String VIEW = "product.view";
        }

        static class Type {
            static final String VIEW = "PRODUCT_VIEW";
        }

        record Viewed(Long productId) {
            public static Viewed of(Long productId) {
                return new Viewed(productId);
            }
        }
    }
}

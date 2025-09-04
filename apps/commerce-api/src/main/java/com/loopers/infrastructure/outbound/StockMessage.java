package com.loopers.infrastructure.outbound;

public class StockMessage {
    public static class TOPIC {
        public static final String CHANGE = "product.stock-changed";
        public static final String OUT = "product.stock-out";
    }

    public static class V1 {
        public static final String VERSION = "v1";

        public static class Type {
            public static final String CHANGE = "STOCK_CHANGED:V1";
            public static final String OUT = "STOCK_OUT:V1";
        }

        public record Changed(Long productId, Integer quantity) {
            public static Changed sale(Long productId, Integer quantity) {
                return new Changed(productId, quantity);
            }

            public static Changed cancel(Long productId, Integer quantity) {
                return new Changed(productId, -quantity);
            }
        }

        public record Out(Long productId) {
            public static Out of(Long productId) {
                return new Out(productId);
            }
        }
    }
}

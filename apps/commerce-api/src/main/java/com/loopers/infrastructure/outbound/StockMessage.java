package com.loopers.infrastructure.outbound;

public class StockMessage {
    public static String TOPIC = "stock-event";

    public static class V1 {
        public static final String VERSION = "v1";

        public static class Type {
            public static final String CHANGE = "STOCK_CHANGED:V1";
            public static final String SOLD_OUT = "STOCK_SOLD_OUT:V1";
        }

        public record Changed(Long productId, Integer quantity) {
            public static Changed sale(Long productId, Integer quantity) {
                return new Changed(productId, quantity);
            }

            public static Changed cancel(Long productId, Integer quantity) {
                return new Changed(productId, -quantity);
            }
        }

        public record SoldOut(Long productId) {
            public static SoldOut of(Long productId) {
                return new SoldOut(productId);
            }
        }
    }
}

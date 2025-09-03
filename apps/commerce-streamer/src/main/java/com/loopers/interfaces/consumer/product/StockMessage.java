package com.loopers.interfaces.consumer.product;

import com.loopers.domain.product.ProductMetricCommand;

import java.time.LocalDate;

public class StockMessage {
    public static class TOPIC {
        public static final String CHANGE = "product.stock-changed";
        public static final String OUT = "product.stock-out";
    }

    public static class V1 {
        public static final String VERSION = "v1";

        public static class Type {
            public static final String CHANGED = "STOCK_CHANGED";
            public static final String OUT = "STOCK_OUT";
        }

        public record Changed(Long productId, Integer quantity) {

            public ProductMetricCommand.StockChange toCommand(LocalDate date) {
                return ProductMetricCommand.StockChange.of(productId, date, quantity.longValue());
            }
        }

        public record Out(Long productId) {
            public static Out of(Long productId) {
                return new Out(productId);
            }
        }
    }
}

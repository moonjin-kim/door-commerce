package com.loopers.interfaces.consumer.product;

import com.loopers.domain.product.ProductMetricCommand;

import java.time.LocalDate;

public class StockMessage {
    public static final String TOPIC = "stock-event";

    public static class V1 {
        public static final String VERSION = "v1";

        public static class Type {
            public static final String CHANGED = "STOCK_CHANGED:V1";
            public static final String OUT = "STOCK_OUT:V1";
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

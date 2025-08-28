package com.loopers.application.stock;

import com.loopers.domain.stock.StockCommand;

public class StockEvent {

    public record Increase(Long productId, int quantity) {
        public static StockEvent.Increase of(Long productId, int quantity) {
            return new StockEvent.Increase(productId, quantity);
        }

        public StockCommand.Rollback toCommand() {
            return StockCommand.Rollback.of(productId, quantity);
        }
    }

    public record Decrease(Long productId, int quantity) {
        public static StockEvent.Decrease of(Long productId, int quantity) {
            return new StockEvent.Decrease(productId, quantity);
        }

        public StockCommand.Consume toCommand() {
            return StockCommand.Consume.of(productId, quantity);
        }
    }
}

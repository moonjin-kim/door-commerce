package com.loopers.domain.stock;

public class StockEvent {
    public record Out(Long productId) {
        public static Out of(Long productId) {
            return new Out(productId);
        }
    }

    public record Consumed(Long productId, Integer quantity) {
        public static Consumed of(Long productId, Integer quantity) {
            return new Consumed(productId, quantity);
        }
    }

    public record Rollback(Long productId, Integer quantity) {
        public static Rollback of(Long productId, Integer quantity) {
            return new Rollback(productId, quantity);
        }
    }
}

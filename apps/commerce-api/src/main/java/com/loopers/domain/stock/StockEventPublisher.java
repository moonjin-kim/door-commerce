package com.loopers.domain.stock;

public interface StockEventPublisher {
    public record Inquiry(Long productId) {
        public static Inquiry of(Long productId) {
            return new Inquiry(productId);
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

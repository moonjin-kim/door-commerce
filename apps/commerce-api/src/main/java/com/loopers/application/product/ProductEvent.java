package com.loopers.application.product;

public class ProductEvent {
    public record View(Long productId) {
        public static View of(Long productId) {
            return new View(productId);
        }
    }
}

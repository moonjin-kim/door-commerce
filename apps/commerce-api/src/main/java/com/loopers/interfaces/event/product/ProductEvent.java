package com.loopers.interfaces.event.product;

public class ProductEvent {
    public record IncreaseLikeCount(Long productId) {
        public static IncreaseLikeCount of(Long productId) {
            return new IncreaseLikeCount(productId);
        }
    }

    public record DecreaseLikeCount(Long productId) {
        public static DecreaseLikeCount of(Long productId) {
            return new DecreaseLikeCount(productId);
        }
    }
}

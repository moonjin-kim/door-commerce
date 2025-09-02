package com.loopers.domain.product;

public class ProductEvent {
    public record Inquiry(Long productId) {
    }
}

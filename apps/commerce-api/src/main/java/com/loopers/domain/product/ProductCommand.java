package com.loopers.domain.product;

public class ProductCommand {
    public record Create(Long brandId, String name, String description, String imageUrl, Long price) {
        public static Create of(Long brandId, String name, String description, String imageUrl, Long price) {
            return new Create(brandId, name, description, imageUrl, price);
        }
    }
}

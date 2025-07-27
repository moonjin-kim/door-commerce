package com.loopers.application.product;

import com.loopers.domain.product.Product;

public class ProductResult {
    public record ProductDto(
            Long id,
            Long brandId,
            String name,
            String description,
            String imageUrl,
            Long price
    ) {
        public static ProductDto of(Product product) {
            return new ProductDto(
                    product.getId(),
                    product.getBrandId(),
                    product.getName(),
                    product.getDescription(),
                    product.getImageUrl(),
                    product.getPrice().getPrice()
            );
        }
    }
}

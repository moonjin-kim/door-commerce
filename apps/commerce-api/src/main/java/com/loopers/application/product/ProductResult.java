package com.loopers.application.product;

import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductInfo;

import java.util.List;

public class ProductResult {
    public record ProductDto(
            Long id,
            Long brandId,
            String name,
            String description,
            String imageUrl,
            Long price
    ) {
        public static ProductDto of(ProductInfo product) {
            return new ProductDto(
                    product.id(),
                    product.brandId(),
                    product.name(),
                    product.description(),
                    product.imageUrl(),
                    product.price()
            );
        }
    }
}

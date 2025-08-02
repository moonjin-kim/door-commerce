package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductResult;

public class ProductV1Response {
    public record ProductDetail(
            Long productId,
            Long brandId,
            String brandName,
            String name,
            String description,
            String imageUrl,
            Long price,
            long lickCount,
            boolean isLiked
    ) {
        public static ProductDetail of(ProductResult.ProductDetail product) {
            return new ProductDetail(
                    product.id(),
                    product.brandId(),
                    product.brandName(),
                    product.name(),
                    product.description(),
                    product.imageUrl(),
                    product.price(),
                    product.likeCount(),
                    product.isLiked()
            );
        }
    }

    public record ProductSummary(
            Long productId,
            Long brandId,
            String name,
            String imageUrl,
            Long price
    ) {
        public static ProductSummary of(ProductResult.ProductDto product) {
            return new ProductSummary(
                    product.id(),
                    product.brandId(),
                    product.name(),
                    product.imageUrl(),
                    product.price()
            );
        }
    }

}

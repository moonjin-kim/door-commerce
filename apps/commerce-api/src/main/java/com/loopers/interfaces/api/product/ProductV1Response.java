package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductResult;

public class ProductV1Response {
    public record ProductDetail(
            Long productId,
            Long brandId,
            String name,
            String description,
            String imageUrl,
            Long price
//            long lickCount,
//            boolean isLiked,
    ) {
        public static ProductDetail of(ProductResult.ProductDto product) {
            return new ProductDetail(
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

package com.loopers.application.product;

import com.loopers.domain.brand.BrandInfo;
import com.loopers.domain.product.ProductInfo;

public class ProductResult {
    public record ProductDto(
            Long id,
            Long brandId,
            String name,
            String description,
            String imageUrl,
            Long price
    ) {
        public static ProductDto from(ProductInfo product) {
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

    public record ProductDetail(
            Long id,
            Long brandId,
            String brandName,
            String name,
            String description,
            String imageUrl,
            Long price,
            Boolean isLiked
    ) {
        public static ProductDetail from(ProductInfo product, BrandInfo brandInfo, Boolean isLiked) {
            return new ProductDetail(
                    product.id(),
                    product.brandId(),
                    brandInfo.name(),
                    product.name(),
                    product.description(),
                    product.imageUrl(),
                    product.price(),
                    isLiked
            );
        }
    }
}

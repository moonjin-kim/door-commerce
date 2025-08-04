package com.loopers.application.product;

import com.loopers.domain.brand.BrandInfo;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductView;

public class ProductResult {
    public record ProductDto(
            Long id,
            Long brandId,
            String name,
            String description,
            String imageUrl,
            Long price,
            Long likeCount
    ) {
        public static ProductDto from(ProductView product) {
            return new ProductDto(
                    product.getId(),
                    product.getBrandId(),
                    product.getName(),
                    product.getDescription(),
                    product.getImageUrl(),
                    product.getPrice(),
                    product.getLikeCount()
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
            Long likeCount,
            Boolean isLiked
    ) {
        public static ProductDetail from(ProductInfo product, BrandInfo brandInfo, Boolean isLiked, Long likeCount) {
            return new ProductDetail(
                    product.id(),
                    product.brandId(),
                    brandInfo.name(),
                    product.name(),
                    product.description(),
                    product.imageUrl(),
                    product.price(),
                    likeCount,
                    isLiked
            );
        }
    }
}

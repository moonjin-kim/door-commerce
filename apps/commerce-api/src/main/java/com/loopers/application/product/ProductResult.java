package com.loopers.application.product;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandInfo;
import com.loopers.domain.product.Product;
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
                    product.getPrice().longValue(),
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
        public static ProductDetail from(Product product, Brand brand, Boolean isLiked, Long likeCount) {
            return new ProductDetail(
                    product.getId(),
                    product.getBrandId(),
                    brand.getName(),
                    product.getName(),
                    product.getDescription(),
                    product.getImageUrl(),
                    product.getPrice().longValue(),
                    likeCount,
                    isLiked
            );
        }
    }
}

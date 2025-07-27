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

    public record ProductPage(
            Long totalCount,
            int limit,
            Long offset,
            List<ProductDto> products
    ) {
        public static ProductPage of(ProductInfo.ProductPage productPage) {
            return new ProductPage(
                    productPage.totalElements(),
                    productPage.limit(),
                    productPage.offset(),
                    productPage.items().stream()
                            .map(ProductDto::of)
                            .toList()
            );
        }
    }
}

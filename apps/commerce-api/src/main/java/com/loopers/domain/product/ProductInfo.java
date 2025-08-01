package com.loopers.domain.product;

public record ProductInfo(
        Long id,
        Long brandId,
        String name,
        String description,
        String imageUrl,
        Long price
) {
    static public ProductInfo of(
            Product product
    ) {
        return new ProductInfo(
                product.getId(),
                product.getBrandId(),
                product.getName(),
                product.getDescription(),
                product.getImageUrl(),
                product.getPrice().value()
        );
    }
}

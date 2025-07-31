package com.loopers.domain.product;

import com.loopers.infrastructure.product.ProductParams;

public class ProductCommand {
    public record Create(
            Long brandId,
            String name,
            String description,
            String imageUrl,
            Long price
    ) {
        public static Create of(Long brandId, String name, String description, String imageUrl, Long price) {
            return new Create(brandId, name, description, imageUrl, price);
        }
    }


    public record Search(
            String sort,
            Long brandId
    ) {
        public static Search of(String sort, Long brandId) {
            return new Search(sort, brandId);
        }

        public ProductParams.Search toParams() {
            return ProductParams.Search.of(sort, brandId);
        }
    }
}

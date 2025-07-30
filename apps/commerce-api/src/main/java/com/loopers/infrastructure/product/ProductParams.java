package com.loopers.infrastructure.product;

public class ProductParams {
    public record Search(
            String sort,
            Long brandId
    ) {
        public static Search of(String sort, Long brandId) {
            return new Search(sort, brandId);
        }
    }
}

package com.loopers.infrastructure.product;

public class ProductParams {
    public record Search(
            int limit,
            long offset,
            String sort,
            Long brandId
    ) {
        public static Search of(int limit, long offset, String sort, Long brandId) {
            return new Search(limit, offset, sort, brandId);
        }
    }
}

package com.loopers.domain.product;

import com.loopers.infrastructure.product.ProductParams;

public class ProductQuery {
    public record Search(
            int limit,
            long offset,
            String sort,
            Long brandId
    ) {
        public static Search of(int limit, long offset, String sort, Long brandId) {
            return new Search(limit, offset, sort, brandId);
        }

        public ProductParams.Search toParams() {
            return ProductParams.Search.of(limit, offset, sort, brandId);
        }
    }
}

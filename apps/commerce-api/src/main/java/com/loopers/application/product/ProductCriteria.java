package com.loopers.application.product;

import com.loopers.domain.product.ProductCommand;

public class ProductCriteria {
    public record Search(
            String sort,
            Long brandId
    ) {
        public static Search of(
                String sort,
                Long brandId
        ) {
            return new Search(sort, brandId);
        }

        public ProductCommand.Search toCommand() {
            return ProductCommand.Search.of(
                    sort,
                    brandId
            );
        }
    }
}

package com.loopers.infrastructure.product;

import com.loopers.domain.product.ProductCommand;

public class ProductParams {
    public record Search(
            ProductSortOption sort,
            Long brandId
    ) {
        public static Search of(String sort, Long brandId) {
            return new Search(
                    ProductSortOption.fromValue(sort),
                    brandId
            );
        }
    }

    public record SearchCount(
            Long brandId
    ) {
        public static SearchCount of(Long brandId) {
            return new SearchCount(
                    brandId
            );
        }
    }

    public enum ProductSortOption {
        PRICE_ASC("price_asc"),
        LATEST("latest"),
        LIKE_DESC("like_desc");

        private final String value;

        ProductSortOption(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static ProductSortOption fromValue(String value) {
            for (ProductSortOption sort : values()) {
                if (sort.value.equals(value)) {
                    return sort;
                }
            }
            throw new IllegalArgumentException("Invalid sort value: " + value);
        }
    }
}

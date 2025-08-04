package com.loopers.application.product;

import com.loopers.domain.product.ProductCommand;

public class ProductCriteria {
    public record Search(
            ProductSortOption sort,
            Long brandId
    ) {
        public static Search of(
                String sort,
                Long brandId
        ) {
            return new Search(
                    ProductSortOption.fromValue(sort),
                    brandId
            );
        }

        public ProductCommand.Search toCommand() {
            return ProductCommand.Search.of(
                    sort.getValue(),
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
            return LATEST; // 기본값으로 LATEST를 반환
        }
    }
}

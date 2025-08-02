package com.loopers.domain.product;

import com.loopers.application.product.ProductCriteria;
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
            ProductSortOption sort,
            Long brandId
    ) {
        public static Search of(String sort, Long brandId) {
            return new Search(
                    ProductSortOption.fromValue(sort),
                    brandId
            );
        }

        public ProductParams.Search toParams() {
            return ProductParams.Search.of(sort.value, brandId);
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

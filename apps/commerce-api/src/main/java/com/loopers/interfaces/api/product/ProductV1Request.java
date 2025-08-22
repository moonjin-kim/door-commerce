package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductCriteria;
import lombok.Getter;

public class ProductV1Request {
    public record Search(
            Long brandId,
            String sort
    ){
        public ProductCriteria.Search toCriteria() {
            return ProductCriteria.Search.of(
                    sort,
                    brandId
            );
        }
    }

    public record SearchCount(
            Long brandId
    ){
        public ProductCriteria.SearchCount toCriteria() {
            return ProductCriteria.SearchCount.of(
                    brandId
            );
        }
    }
}

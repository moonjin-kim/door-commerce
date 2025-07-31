package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductCriteria;

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

}

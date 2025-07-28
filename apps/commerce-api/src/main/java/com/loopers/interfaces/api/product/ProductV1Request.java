package com.loopers.interfaces.api.product;

import com.loopers.domain.product.ProductQuery;
import org.springframework.data.domain.Pageable;

public class ProductV1Request {
    public record Search(
            int page,
            int size,
            Long brandId,
            String sort
    ){
        public ProductQuery.Search toQuery() {
            return ProductQuery.Search.of(
                    size,
                    getOffset(),
                    sort,
                    brandId
            );
        }

        private Long getOffset() {
            return (long) (page-1) * size;
        }
    }


}

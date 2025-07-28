package com.loopers.domain.product;

import java.util.List;

public class ProductInfo {
    public record ProductPage(
            int limit,
            long offset,
            long totalElements,
            List<Product> items
    ) {

    }
}

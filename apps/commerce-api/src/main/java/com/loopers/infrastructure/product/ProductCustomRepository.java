package com.loopers.infrastructure.product;

import com.loopers.domain.product.ProductInfo;

public interface ProductCustomRepository {
    ProductInfo.ProductPage search(ProductParams.Search productSearch);
}

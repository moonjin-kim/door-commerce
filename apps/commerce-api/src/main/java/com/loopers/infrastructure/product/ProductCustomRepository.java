package com.loopers.infrastructure.product;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductInfo;

public interface ProductCustomRepository {
    PageResponse<Product> search(PageRequest<ProductParams.Search> productSearch);
}

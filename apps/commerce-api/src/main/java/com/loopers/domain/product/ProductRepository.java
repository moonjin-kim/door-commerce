package com.loopers.domain.product;

import com.loopers.infrastructure.product.ProductParams;

import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);

    Optional<Product> findBy(Long id);

    ProductInfo.ProductPage search(ProductParams.Search productSearch);
}

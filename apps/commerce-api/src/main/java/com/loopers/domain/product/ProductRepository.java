package com.loopers.domain.product;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.infrastructure.product.ProductParams;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);

    List<Product> findAllBy(List<Long> productIds);

    Optional<Product> findBy(Long id);

    PageResponse<ProductView> search(PageRequest<ProductParams.Search> productSearch);
    Long searchCount(ProductParams.SearchCount productSearch);

}

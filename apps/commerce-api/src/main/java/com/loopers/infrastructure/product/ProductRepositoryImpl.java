package com.loopers.infrastructure.product;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.product.ProductView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductJpaRepository productJpaRepository;
    private final ProductQueryDslRepository productQueryDslRepository;

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public List<Product> findAllBy(List<Long> productIds) {
        return productJpaRepository.findAllById(productIds);
    }

    @Override
    public Optional<Product> findBy(Long id) {
        return productJpaRepository.findById(id);
    }

    @Override
    public PageResponse<ProductView> search(PageRequest<ProductParams.Search> productSearch) {
        return productQueryDslRepository.search(productSearch);
    }

    @Override
    public Long searchCount(ProductParams.SearchCount productSearch) {
        return productQueryDslRepository.searchCount(productSearch);
    }
}

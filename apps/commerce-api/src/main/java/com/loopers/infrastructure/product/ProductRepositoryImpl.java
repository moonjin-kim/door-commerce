package com.loopers.infrastructure.product;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductJpaRepository productJpaRepository;
    private final ProductCustomRepository productCustomRepository;

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
    public PageResponse<Product> search(PageRequest<ProductParams.Search> productSearch) {
        return productCustomRepository.search(productSearch);
    }
}

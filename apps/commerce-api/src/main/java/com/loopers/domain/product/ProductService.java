package com.loopers.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;

    public Optional<Product> getBy(Long id) {
        return productRepository.findBy(id);
    }

    public ProductInfo.ProductPage search(ProductQuery.Search query) {
        return productRepository.search(query.toParams());
    }
}

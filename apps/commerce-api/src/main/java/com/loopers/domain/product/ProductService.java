package com.loopers.domain.product;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
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

    @Transactional
    public void decreaseLikeCount(Long productId) {
        Product product = productRepository.findBy(productId).orElseThrow(
                () -> new CoreException(ErrorType.NOT_FOUND,"Product not found with id: " + productId)
        );

        product.decreaseLikeCount();
    }

    @Transactional
    public void increaseLikeCount(Long productId) {
        Product product = productRepository.findBy(productId).orElseThrow(
                () -> new CoreException(ErrorType.NOT_FOUND,"Product not found with id: " + productId)
        );

        product.increaseLikeCount();
    }
}

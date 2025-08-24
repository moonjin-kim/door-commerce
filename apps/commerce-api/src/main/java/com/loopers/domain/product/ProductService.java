package com.loopers.domain.product;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.infrastructure.product.ProductParams;
import com.loopers.support.cache.CacheRepository;
import com.loopers.support.cache.CommerceCache;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CacheRepository cacheRepository;


    public void increaseLikeCount(Long productId) {
        Product product = productRepository.findBy(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

        product.increaseLikeCount();
    }

    public void decreaseLikeCount(Long productId) {
        Product product = productRepository.findBy(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

        product.decreaseLikeCount();
    }

    public Optional<Product> getBy(Long id) {
        Optional<Product> cachedProduct = cacheRepository.get(
                CommerceCache.ProductCache.INSTANCE,
                id.toString(),
                Product.class
        );
        if (cachedProduct.isPresent()) {
            return cachedProduct;
        }

        Optional<Product> product = productRepository.findBy(id);

        product.ifPresent(value -> cacheRepository.set(CommerceCache.ProductCache.INSTANCE, id.toString(), value));

        return product;
    }

    @CircuitBreaker(name = "productCircuit", fallbackMethod = "getProductsFallback")
    public PageResponse<ProductView> search(PageRequest<ProductCommand.Search> command) {
        PageRequest<ProductParams.Search> productParams = command.map(ProductCommand.Search::toParams);

        return productRepository.search(productParams);
    }

    public Long searchCount(ProductCommand.SearchCount command) {
        // 조회 캐시 조회
        Optional<Long> cachedProduct = cacheRepository.get(CommerceCache.ProductSearchCountCache.INSTANCE, command.toString(), Long.class);
        if (cachedProduct.isPresent()) {
            return cachedProduct.get();
        }

        Long count = productRepository.searchCount(command.toParams());

        cacheRepository.set(
                CommerceCache.ProductSearchCountCache.INSTANCE,
                command.toParams().toString(),
                count
        );

        return count;
    }

    public List<ProductInfo> findAllBy(List<Long> productIds) {
        return productRepository.findAllBy(productIds).stream().map(ProductInfo::of).collect(Collectors.toList());
    }

    private PageResponse<ProductView> getProductsFallback(PageRequest<ProductCommand.Search> command, Throwable t) {
        log.error("Fallback for getProducts executed. Command: {}, Error: {}",
                command, t.getMessage());

        // 그 외의 경우, 가장 안전한 빈 페이지를 반환
        return PageResponse.of(
                command.getPage(),
                command.getSize(),
                Collections.emptyList()
        );
    }
}

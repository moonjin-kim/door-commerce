package com.loopers.domain.product;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.infrastructure.product.ProductParams;
import com.loopers.support.CacheRepository;
import com.loopers.support.cache.CommerceCache;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;
    private final CacheRepository cacheRepository;

    public Optional<Product> getBy(Long id) {
        Optional<Product> cachedProduct = cacheRepository.get(CommerceCache.ProductCache.INSTANCE, id.toString(), Product.class);
        if (cachedProduct.isPresent()) {
            return cachedProduct;
        }

        Optional<Product> product = productRepository.findBy(id);

        product.ifPresent(value -> cacheRepository.set(CommerceCache.ProductCache.INSTANCE, id.toString(), value));

        return product;
    }

    public PageResponse<ProductView> search(PageRequest<ProductCommand.Search> command) {
        PageRequest<ProductParams.Search> productParams = command.map(ProductCommand.Search::toParams);

        return productRepository.search(productParams);
    }

    public Long searchCount(ProductCommand.SearchCount command) {
        Optional<Long> cachedProduct = cacheRepository.get(CommerceCache.ProductSearchCache.INSTANCE, command.toString(), Long.class);
        if (cachedProduct.isPresent()) {
            return cachedProduct.get();
        }

        Long count = productRepository.searchCount(command.toParams());

        cacheRepository.set(
                CommerceCache.ProductSearchCache.INSTANCE,
                command.toParams().toString(),
                count
        );

        return count;
    }

    public List<ProductInfo> findAllBy(List<Long> productIds) {
        return productRepository.findAllBy(productIds).stream().map(ProductInfo::of).collect(Collectors.toList());
    }
}

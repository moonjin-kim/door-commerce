package com.loopers.applicaiton.product;

import com.loopers.support.cache.CacheRepository;
import com.loopers.support.cache.CommerceCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductCacheFacade {
    private final CacheRepository cacheRepository;

    public void removeCache(Long productId) {
        cacheRepository.delete(CommerceCache.ProductCache.INSTANCE, productId.toString());
    }
}

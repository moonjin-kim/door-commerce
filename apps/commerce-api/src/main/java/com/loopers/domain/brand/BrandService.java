package com.loopers.domain.brand;

import com.loopers.domain.product.Product;
import com.loopers.support.CacheRepository;
import com.loopers.support.cache.CommerceCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {
    private final BrandRepository brandRepository;
    private final CacheRepository cacheRepository;

    public Optional<Brand> getBy(Long id) {
        Optional<Brand> cachedBrand = cacheRepository.get(
                CommerceCache.ProductCache.INSTANCE,
                id.toString(),
                Brand.class
        );
        if (cachedBrand.isPresent()) {
            return cachedBrand;
        }
        Optional<Brand> brand = brandRepository.findBy(id);

        brand.ifPresent(value -> cacheRepository.set(
                CommerceCache.BrandCache.INSTANCE,
                id.toString(),
                value
        ));

        return brand;
    }
}

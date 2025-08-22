package com.loopers.support.cache;

import java.time.Duration;

public class CommerceCache {
    public static final class ProductCache extends CacheKey {
        private ProductCache() {
            super("상품 정보", Duration.ofMinutes(10), "v1");
        }
        public static final ProductCache INSTANCE = new ProductCache();
    }

    public static final class ProductSearchCountCache extends CacheKey {
        private ProductSearchCountCache() {
            super("상품 검색 결과", Duration.ofMinutes(10), "v1");
        }
        public static final ProductSearchCountCache INSTANCE = new ProductSearchCountCache();
    }

    public static final class BrandCache extends CacheKey {
        private BrandCache() {
            super("브랜드 조회", Duration.ofMinutes(10), "v1");
        }
        public static final BrandCache INSTANCE = new BrandCache();
    }

}

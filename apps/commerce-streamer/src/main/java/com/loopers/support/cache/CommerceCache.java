package com.loopers.support.cache;

import java.time.Duration;

public class CommerceCache {
    public static final class ProductCache extends CacheKey {
        private ProductCache() {
            super("상품 정보", Duration.ofMinutes(1), "v1");
        }
        public static final ProductCache INSTANCE = new ProductCache();
    }
}

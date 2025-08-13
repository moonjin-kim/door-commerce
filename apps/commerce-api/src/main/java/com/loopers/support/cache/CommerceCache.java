package com.loopers.support.cache;

import com.loopers.support.MyCache;

import java.time.Duration;

public class CommerceCache {
    public static final class ProductCache extends MyCache {
        private ProductCache() {
            super("상품 정보", Duration.ofHours(6));
        }
        public static final ProductCache INSTANCE = new ProductCache();
    }
}

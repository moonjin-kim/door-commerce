package com.loopers.support.cache;

import com.loopers.support.MyCache;

import java.time.Duration;

public class CommerceCache {
    public static final class ProductCache extends MyCache {
        private ProductCache() {
            super("상품 정보", Duration.ofMinutes(10));
        }
        public static final ProductCache INSTANCE = new ProductCache();
    }

    public static final class ProductSearchCache extends MyCache {
        private ProductSearchCache() {
            super("상품 검색 결과", Duration.ofMinutes(1));
        }
        public static final ProductSearchCache INSTANCE = new ProductSearchCache();
    }

}

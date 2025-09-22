package com.loopers.support.cache;

import java.time.Duration;

public class CommerceCache {
    public static final class ProductCache extends CacheKey {
        private ProductCache() {
            super("상품 정보", Duration.ofMinutes(1), "v1");
        }
        public static final ProductCache INSTANCE = new ProductCache();
    }

    public static final class RankingCache extends CacheKey {
        private RankingCache() {
            super("상품 랭킹", Duration.ofHours(48), "v1");
        }

        @Override
        public String getKey(String key) {
            return "rank:all:" + key;
        }

        public static final RankingCache INSTANCE = new RankingCache();
    }
}

package com.loopers.infrastructure.ranking;

import com.loopers.support.cache.CacheKey;
import com.loopers.support.cache.CommerceCache;

import java.time.Duration;

public class RankingWeightKey {
    public static final class ProductWeight extends CacheKey {
        private ProductWeight() {
            super("랭킹 가중치", null, "v1");
        }
        public static final RankingWeightKey.ProductWeight INSTANCE = new RankingWeightKey.ProductWeight();
    }

}

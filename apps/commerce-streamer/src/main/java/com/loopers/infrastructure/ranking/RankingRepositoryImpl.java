package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.RankingRepository;
import com.loopers.support.cache.CacheKey;
import com.loopers.support.cache.CacheRepository;
import com.loopers.support.cache.CommerceCache;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RankingRepositoryImpl implements RankingRepository {
    private final CacheRepository cacheRepository;

    @Override
    public void updateProductRankings(String key, Set<ZSetOperations.TypedTuple<String>> scores) {
        cacheRepository.zadd(CommerceCache.RankingCache.INSTANCE, key, scores);
    }

    @Override
    public void updateProductRanking(String key, String productId, double score) {
        cacheRepository.zadd(CommerceCache.RankingCache.INSTANCE, key, productId, score);
    }

    @Override
    public Set<String> getRanking(String key, int start, int end) {
        return cacheRepository.zrevrange(CommerceCache.RankingCache.INSTANCE, key, start, end);
    }

    @Override
    public double getScoreBy(String key, String productId) {
        return cacheRepository.getScoreBy(CommerceCache.RankingCache.INSTANCE, key, productId);
    }
}

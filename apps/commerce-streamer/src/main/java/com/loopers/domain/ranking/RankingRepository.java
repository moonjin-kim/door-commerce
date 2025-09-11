package com.loopers.domain.ranking;

import com.loopers.support.cache.CacheKey;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

public interface RankingRepository {
    void updateProductRankings(CacheKey cacheKey, String key, Set<ZSetOperations.TypedTuple<String>> scores);
    void updateProductRanking(CacheKey cacheKey, String key, String productId, double score);
    Set<String> getRanking(CacheKey cacheKey, String key, int start, int end);
    double getScoreBy(CacheKey cacheKey, String key, String productId);
    void createTomorrowRanking(CacheKey cacheKey, LocalDate toDay, Double weight);
    void setExpire(CacheKey cacheKey, String key, Duration seconds);
}

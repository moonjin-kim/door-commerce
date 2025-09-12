package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.RankingRepository;
import com.loopers.support.cache.CacheKey;
import com.loopers.support.cache.CommerceCache;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.zset.Aggregate;
import org.springframework.data.redis.connection.zset.Weights;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

import static java.time.format.DateTimeFormatter.ofPattern;

@Component
@RequiredArgsConstructor
public class RankingRedisRepository implements RankingRepository {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void updateProductRankings(CacheKey cacheKey, String key, Set<ZSetOperations.TypedTuple<String>> scores) {
        redisTemplate.opsForZSet().add(cacheKey.getKey(key), scores);
    }

    @Override
    public void updateProductRanking(CacheKey cacheKey,String key, String productId, double score) {
        redisTemplate.opsForZSet().add(cacheKey.getKey(key), productId, score);
    }

    @Override
    public Set<String> getRanking(CacheKey cacheKey,String key, int start, int end) {
        return redisTemplate.opsForZSet().reverseRange(cacheKey.getKey(key), start, end);
    }

    @Override
    public double getScoreBy(CacheKey cacheKey, String key, String productId) {
        Double score = redisTemplate.opsForZSet().score(cacheKey.getKey(key), productId);
        return (score != null) ? score : 0.0;
    }

    @Override
    public void createTomorrowRanking(CacheKey cacheKey, LocalDate toDay, Double weight) {
        String todayKey = cacheKey.getKey("ranking:daily:" + toDay.format(ofPattern("yyyyMMdd")));
        String tomorrowKey = cacheKey.getKey("ranking:daily:" + toDay.plusDays(1).format(ofPattern("yyyyMMdd")));

        redisTemplate.opsForZSet()
                .unionAndStore(todayKey, Collections.emptyList(), tomorrowKey, Aggregate.SUM, Weights.of(weight));
      
        redisTemplate.expire(tomorrowKey, Duration.ofDays(2));
    }

    @Override
    public void setExpire(CacheKey cache, String key, Duration ttl) {
        redisTemplate.expire(cache.getKey(key), ttl);
    }
}

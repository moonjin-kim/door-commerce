package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.RankingRepository;
import com.loopers.support.cache.CacheKey;
import com.loopers.support.cache.CacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class RankingRedisRepository implements RankingRepository {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public Set<String> getRanking(CacheKey cache, String key, int page, int size) {
        long start = (long) size * (page - 1);
        long end = start + size - 1;

        return redisTemplate.opsForZSet().reverseRange(cache.getKey(key), start, end);
    }

    @Override
    public Long getRank(CacheKey cache, String key, Long memberId) {
        if (memberId == null) return null;
        return redisTemplate.opsForZSet().reverseRank(cache.getKey(key), String.valueOf(memberId));
    }
}

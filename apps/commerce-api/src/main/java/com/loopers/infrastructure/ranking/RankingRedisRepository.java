package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.DaliyRankingRepository;
import com.loopers.support.cache.CacheKey;
import com.loopers.support.cache.CommerceCache;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RankingRedisRepository implements DaliyRankingRepository {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public List<Long> getRanking(String key, int page, int size) {
        CacheKey cache = CommerceCache.RankingCache.INSTANCE;
        long start = (long) size * (page - 1);
        long end = start + size - 1;

        var ids = redisTemplate.opsForZSet().reverseRange(cache.getKey(key), start, end).stream().toList();
        if (ids.isEmpty()) return List.of();
        return ids.stream().map(Long::valueOf).toList();
    }

    @Override
    public Long getRank( String key, Long memberId) {
        CacheKey cache = CommerceCache.RankingCache.INSTANCE;
        if (memberId == null) return null;
        return redisTemplate.opsForZSet().reverseRank(cache.getKey(key), String.valueOf(memberId));
    }
}

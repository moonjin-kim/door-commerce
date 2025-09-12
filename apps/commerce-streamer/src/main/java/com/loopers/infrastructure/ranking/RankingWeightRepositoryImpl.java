package com.loopers.infrastructure.ranking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.domain.ranking.RankingWeight;
import com.loopers.domain.ranking.RankingWeightRepository;
import com.loopers.support.cache.CacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingWeightRepositoryImpl implements RankingWeightRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public RankingWeight save(String key, RankingWeight rankingWeight) {
        try {
            String jsonValue = objectMapper.writeValueAsString(rankingWeight);
            redisTemplate.opsForValue().set(key, jsonValue);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object for cache. key: {}, value: {}", key, rankingWeight, e);
            throw new RuntimeException("Failed to serialize object for cache", e);
        }

        return rankingWeight;
    }

    @Override
    public Optional<RankingWeight> findBy(String key) {
        String valueFromCache = redisTemplate.opsForValue().get(key);

        // 2. 캐시된 값이 없으면 empty를 반환한다.
        if (valueFromCache == null || valueFromCache.isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(objectMapper.readValue(valueFromCache, RankingWeight.class));
        } catch (Exception e) {
            log.error("캐시 데이터 매핑 실패 {}", e.getMessage());
            return Optional.empty();
        }
    }
}

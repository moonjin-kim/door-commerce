package com.loopers.support.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheRedisRepository implements CacheRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public <T> Optional<T> get(CacheKey cache, String key, Class<T> clazz) {
        String valueFromCache = redisTemplate.opsForValue().get(cache.getKey(key));

        // 2. 캐시된 값이 없으면 empty를 반환한다.
        if (valueFromCache == null || valueFromCache.isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(objectMapper.readValue(valueFromCache, clazz));
        } catch (Exception e) {
            log.error("캐시 데이터 매핑 실패 {}", e.getMessage());
            return Optional.empty();
        }
    }


    @Override
    public void set(CacheKey cache, String key, Object value) {
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(cache.getKey(key), jsonValue, cache.getTtl());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object for cache. key: {}, value: {}", key, value, e);
        }
    }

    @Override
    public void delete(CacheKey cache, String key) {
        redisTemplate.delete(cache.getKey(key));
    }
}

package com.loopers.support;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheRedisRepository implements CacheRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public <T> Optional<T> get(MyCache cache, String key, Class<T> clazz) {
        String valueFromCache = redisTemplate.opsForValue().get(cache.getKey(key));

        // 2. 캐시된 값이 없으면 null을 반환합니다.
        if (valueFromCache == null || valueFromCache.isEmpty()) {
            return Optional.empty();
        }

        try {
            // 3. ObjectMapper를 사용해 JSON 문자열을 지정된 클래스(clazz)의 객체로 변환합니다.
            return Optional.of(objectMapper.readValue(valueFromCache, clazz));
        } catch (Exception e) {
            return Optional.empty();
        }
    }


    @Override
    public void set(MyCache cache, String key, Object value) {
        redisTemplate.opsForValue().set(cache.getKey(key), value.toString(), cache.getTtl());
    }

    @Override
    public void delete(MyCache cache, String key) {
        redisTemplate.delete(cache.getKey(key));
    }
}

package com.loopers.support.cache;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

public interface CacheRepository {
    /**
     * 캐시에서 값을 가져옵니다.
     */
    <T> Optional<T> get(CacheKey cache, String key, Class<T> clazz);

    /**
     * 캐시에 값을 저장합니다.
     */
    void set(CacheKey cache, String key, Object value);

    /**
     * 캐시에서 특정 키의 값을 제거합니다.
     */
    void delete(CacheKey cache, String key);

    boolean zadd(CacheKey cache, String key, String member, double score);

    Long getRank(CacheKey cache, String key, String member);

    double getScoreBy(CacheKey cache, String key, String member);

    Set<String> zrevrange(CacheKey cache, String key, int page, int size);

    /** TTL 설정 */
    void expire(CacheKey cache, String key, Duration ttl);
}


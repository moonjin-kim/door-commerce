package com.loopers.support;

import java.time.Duration;
import java.util.Optional;

public interface CacheRepository {
    /**
     * 캐시에서 값을 가져옵니다.
     */
    <T> Optional<T> get(MyCache cache, String key, Class<T> clazz);

    /**
     * 캐시에 값을 저장합니다.
     */
    void set(MyCache cache, String key, Object value);

    /**
     * 캐시에서 특정 키의 값을 제거합니다.
     */
    void delete(MyCache cache, String key);
}


package com.loopers.support;

import java.time.Duration;

/**
 * 모든 캐시 정의의 기본이 되는 추상 클래스입니다.
 * 상속받는 클래스는 캐시의 속성을 정의합니다.
 */
public abstract class MyCache {
    private final String description;
    private final Duration ttl;

    protected MyCache(String description, Duration ttl) {
        this.description = description;
        this.ttl = ttl;
    }

    /**
     * 캐시의 이름으로 클래스의 간단한 이름을 사용합니다. (e.g., "UserProfileCache")
     */
    public String getName() {
        return this.getClass().getSimpleName();
    }

    public String getDescription() {
        return description;
    }

    public Duration getTtl() {
        return ttl;
    }

    public String getKey(String key) {
        return getName() + ":" + key;
    }
}

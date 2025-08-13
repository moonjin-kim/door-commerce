package com.loopers.testcontainers;

import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
public class RedisTestContainersConfig {

    // 1. static final로 Redis 컨테이너를 선언합니다.
    private static final GenericContainer<?> redisContainer;

    static {
        // 2. 사용할 Redis 도커 이미지를 지정하고 컨테이너를 설정합니다.
        redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
                .withExposedPorts(6379) // Redis의 기본 포트 6379를 노출합니다.
                .withReuse(true);       // MySQL 설정과 동일하게 재사용 설정을 추가합니다.

        // 3. 컨테이너를 시작합니다.
        redisContainer.start();

        // 4. Spring Boot가 사용할 Redis 접속 정보를 System Property에 설정합니다.
        // 이 프로퍼티 이름은 application.yml에 설정된 값과 일치해야 합니다.
        System.setProperty("spring.data.redis.host", redisContainer.getHost());
        System.setProperty("spring.data.redis.port", redisContainer.getMappedPort(6379).toString());
    }
}

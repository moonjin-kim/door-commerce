package com.loopers.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        // ObjectMapper 설정: Java 8의 시간 타입(LocalDate 등) 지원 및 타입 정보 포함
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .activateDefaultTyping(
                        BasicPolymorphicTypeValidator.builder().allowIfSubType(Object.class).build(),
                        ObjectMapper.DefaultTyping.NON_FINAL
                );

        // Value를 JSON으로 직렬화하는 Serializer 생성
        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Key, Value 및 Hash Key, Hash Value의 직렬화 방식 설정
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jsonRedisSerializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jsonRedisSerializer);
        return template;
    }
}

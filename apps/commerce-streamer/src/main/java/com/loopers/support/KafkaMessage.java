package com.loopers.support;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.loopers.interfaces.consumer.product.LikeMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class KafkaMessage<T> {
    private String eventId;
    private String version;
    private LocalDateTime publishedAt;
    private String eventType;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,              // 어떤 값으로 타입을 구분할지
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY, // 외부 필드(eventType)를 사용
            property = "eventType"                  // eventType 값으로 매핑
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = LikeMessage.V1.Changed.class, name = LikeMessage.V1.Type.CHANGED),
    })
    private T payload;

    public KafkaMessage(String eventId, String version, LocalDateTime publishedAt, String eventType, T payload) {
        this.eventId = eventId;
        this.version = version;
        this.publishedAt = publishedAt;
        this.payload = payload;
        this.eventType = eventType;
    }

    public static <T> KafkaMessage<T> of(String eventId, String version, LocalDateTime publishedAt, String eventType, T payload) {
        return new KafkaMessage<>(eventId, version, publishedAt, eventType, payload);
    }

}

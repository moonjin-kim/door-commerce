package com.loopers.support.kafka;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class KafkaMessage<T> {
    private String eventId;
    private String version;
    private LocalDateTime publishedAt;
    private String eventType;
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

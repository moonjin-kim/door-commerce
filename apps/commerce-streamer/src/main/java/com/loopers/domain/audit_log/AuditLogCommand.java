package com.loopers.domain.audit_log;

import java.time.LocalDateTime;

public class AuditLogCommand {
    public record Save(String topic,String eventId, String version, LocalDateTime publishedAt, String eventType, String payload) {
        public static Save of(String topic,String eventId, String version, LocalDateTime publishedAt, String eventType, String payload) {
            return new Save(topic,eventId, version, publishedAt, eventType, payload);
        }
    }
}

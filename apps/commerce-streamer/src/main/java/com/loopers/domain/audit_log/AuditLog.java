package com.loopers.domain.audit_log;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AuditLog extends BaseEntity {
    @Column(nullable = false)
    String eventId;
    @Column(nullable = false)
    String version;
    @Column(nullable = false)
    LocalDateTime publishedAt;
    @Column(nullable = false)
    String eventType;
    @Column(columnDefinition = "jsonb")
    String payload;

    public AuditLog(String eventId, String version, LocalDateTime publishedAt, String eventType, String payload) {
        this.eventId = eventId;
        this.version = version;
        this.publishedAt = publishedAt;
        this.eventType = eventType;
        this.payload = payload;
    }

    public static AuditLog create(AuditLogCommand.Save command) {
        return new AuditLog(command.eventId(), command.version(), command.publishedAt(), command.eventType(), command.payload());
    }
}

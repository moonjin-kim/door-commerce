package com.loopers.domain.event_hendler;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_handlers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class EventHandler extends BaseEntity {
    @Column(nullable = false)
    String eventId;
    @Column(nullable = false)
    String groupId;

    public EventHandler(String eventId, String groupId) {
        this.eventId = eventId;
        this.groupId = groupId;
    }

    public static EventHandler create(String eventId, String groupId) {
        return new EventHandler(eventId, groupId);
    }
}

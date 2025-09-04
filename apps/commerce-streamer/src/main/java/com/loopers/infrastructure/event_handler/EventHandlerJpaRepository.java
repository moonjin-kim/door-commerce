package com.loopers.infrastructure.event_handler;

import com.loopers.domain.event_hendler.EventHandler;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventHandlerJpaRepository extends JpaRepository<EventHandler, Long> {
    boolean existsByEventIdAndGroupId(String eventId, String groupId);
}

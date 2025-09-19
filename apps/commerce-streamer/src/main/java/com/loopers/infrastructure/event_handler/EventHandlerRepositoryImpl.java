package com.loopers.infrastructure.event_handler;

import com.loopers.domain.event_hendler.EventHandler;
import com.loopers.domain.event_hendler.EventHandlerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventHandlerRepositoryImpl implements EventHandlerRepository {
    private final EventHandlerJpaRepository jpaRepository;

    @Override
    public EventHandler save(EventHandler eventHandler) {
        return jpaRepository.save(eventHandler);
    }

    @Override
    public boolean existBy(String eventId, String groupId) {
        return jpaRepository.existsByEventIdAndGroupId(eventId, groupId);
    }
}

package com.loopers.domain.event_hendler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventHandlerService {
    private final EventHandlerRepository eventHandlerRepository;

    public boolean existEventBy(String eventId, String groupId) {
        return eventHandlerRepository.existBy(eventId, groupId);
    }

    public EventHandler save(String eventId, String groupId) {
        return eventHandlerRepository.save(
                EventHandler.create(eventId, groupId)
        );
    }
}

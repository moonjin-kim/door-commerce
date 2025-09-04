package com.loopers.domain.event_hendler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventHandlerService {
    private final EventHandlerRepository eventHandlerRepository;

    public boolean tryHandle(String eventId, String groupId) {
        if(eventHandlerRepository.existBy(eventId, groupId)) {
            return false;
        }

        eventHandlerRepository.save(
                EventHandler.create(eventId, groupId)
        );
        return true;
    }

}

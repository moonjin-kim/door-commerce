package com.loopers.domain.event_hendler;

public interface EventHandlerRepository {
    EventHandler save(EventHandler eventHandler);
    boolean existBy(String eventId, String groupId);
}

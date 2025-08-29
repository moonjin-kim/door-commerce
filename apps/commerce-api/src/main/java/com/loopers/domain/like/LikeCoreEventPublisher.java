package com.loopers.domain.like;

import io.github.resilience4j.core.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeCoreEventPublisher implements LikeEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(LikeEvent.Like event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publish(LikeEvent.UnLike event) {
        applicationEventPublisher.publishEvent(event);
    }
}

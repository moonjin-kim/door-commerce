package com.loopers.infrastructure.comman;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommonApplicationPublisher implements CommonPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;
    @Override
    public void publish(Object event) {
        applicationEventPublisher.publishEvent(event);
    }
}

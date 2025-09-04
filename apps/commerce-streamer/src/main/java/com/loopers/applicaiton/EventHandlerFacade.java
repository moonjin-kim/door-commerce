package com.loopers.applicaiton;

import com.loopers.domain.event_hendler.EventHandlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventHandlerFacade {
    private final EventHandlerService eventHandlerService;
}

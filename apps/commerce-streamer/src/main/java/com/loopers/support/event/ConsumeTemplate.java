package com.loopers.support.event;

import com.loopers.domain.event_hendler.EventHandlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsumeTemplate {
    private final EventHandlerService handled;

    @Transactional
    public <T> void consume(
            String groupId,
            KafkaMessage<T> msg,
            Runnable domainHandler // 멱등 통과 후 수행할 도메인 처리
    ) {
        // 멱등
        if (!handled.tryHandle(groupId, msg.getEventId())) {
            return; // 이미 처리됨 → 조용히 스킵
        }
        // 실제 비즈니스
        domainHandler.run();
        // @Transactional에 의해 커밋됨 (Listener쪽에서 ACK)
    }
}

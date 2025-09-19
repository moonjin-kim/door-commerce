package com.loopers.support.event;

import com.loopers.domain.event_hendler.EventHandlerService;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ConsumeTemplateTest {

    @Autowired
    private EventHandlerService eventHandlerService;
    @Autowired
    private ConsumeTemplate consumeTemplate;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("메시지를 읽은 적이 없으면 요청을 실행한다.")
    @Test
    void consume_shouldRunDomainHandler_whenIdempotentPass() {
        // given
        String groupId = "group1";
        KafkaMessage<String> msg = mock(KafkaMessage.class);
        when(msg.getEventId()).thenReturn("event-123");

        Runnable domainHandler = mock(Runnable.class);

        // when
        consumeTemplate.consume(groupId, msg, domainHandler);

        // then
        verify(domainHandler, times(1)).run();
    }

    @DisplayName("메시지를 읽은 적이 있으면 요청을 실행하지 않는다.")
    @Test
    void consume_shouldNotRunDomainHandler_whenIdempotentFail() {
        // given
        String groupId = "group1";
        KafkaMessage<String> msg = mock(KafkaMessage.class);
        when(msg.getEventId()).thenReturn("event-123");
        eventHandlerService.tryHandle("event-123", groupId);

        Runnable domainHandler = mock(Runnable.class);

        // when
        consumeTemplate.consume(groupId, msg, domainHandler);

        // then
        verify(domainHandler, never()).run();
    }
}

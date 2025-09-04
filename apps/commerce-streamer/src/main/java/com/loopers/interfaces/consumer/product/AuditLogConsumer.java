package com.loopers.interfaces.consumer.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.applicaiton.product.ProductMetricFacade;
import com.loopers.domain.audit_log.AuditLogCommand;
import com.loopers.domain.audit_log.AuditLogService;
import com.loopers.support.event.ConsumeTemplate;
import com.loopers.support.event.KafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogConsumer {
    private final String GROUP_ID = "audit-log";
    private final AuditLogService auditLogService;
    private final ConsumeTemplate template;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = LikeMessage.TOPIC, groupId = GROUP_ID)
    public void onMessageLike(KafkaMessage<?> msg, Acknowledgment ack) {
        String payloadJson = serializePayload(msg.getPayload());
        template.consume(GROUP_ID, msg, () ->
                auditLogService.createAuditLog(AuditLogCommand.Save.of(
                        LikeMessage.TOPIC,
                        msg.getEventId(),
                        msg.getVersion(),
                        msg.getPublishedAt(),
                        msg.getEventType(),
                        payloadJson
                ))
        );
        ack.acknowledge();
    }

    @KafkaListener(topics = StockMessage.TOPIC, groupId = GROUP_ID)
    public void onMessageStock(KafkaMessage<?> msg, Acknowledgment ack) {
        String payloadJson = serializePayload(msg.getPayload());
        template.consume(GROUP_ID, msg, () ->
                auditLogService.createAuditLog(AuditLogCommand.Save.of(
                        StockMessage.TOPIC,
                        msg.getEventId(),
                        msg.getVersion(),
                        msg.getPublishedAt(),
                        msg.getEventType(),
                        payloadJson
                ))
        );

        ack.acknowledge();
    }

    @KafkaListener(topics = ProductMessage.TOPIC, groupId = GROUP_ID)
    public void onMessageView(KafkaMessage<?> msg, Acknowledgment ack) {
        String payloadJson = serializePayload(msg.getPayload());
        template.consume(GROUP_ID, msg, () ->
                auditLogService.createAuditLog(AuditLogCommand.Save.of(
                        ProductMessage.TOPIC,
                        msg.getEventId(),
                        msg.getVersion(),
                        msg.getPublishedAt(),
                        msg.getEventType(),
                        payloadJson
                ))
        );
        ack.acknowledge();
    }

    private String serializePayload(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            // 실패하면 최소한 빈 객체라도 넣도록 처리
            return "{}";
        }
    }
}

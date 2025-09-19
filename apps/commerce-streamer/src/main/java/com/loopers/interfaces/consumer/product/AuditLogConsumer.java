package com.loopers.interfaces.consumer.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.applicaiton.product.ProductMetricFacade;
import com.loopers.config.kafka.KafkaConfig;
import com.loopers.domain.audit_log.AuditLogCommand;
import com.loopers.domain.audit_log.AuditLogService;
import com.loopers.support.event.ConsumeTemplate;
import com.loopers.support.event.KafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogConsumer {
    private final String GROUP_ID = "audit-log";
    private final AuditLogService auditLogService;
    private final ConsumeTemplate template;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = LikeMessage.TOPIC, groupId = GROUP_ID, containerFactory = KafkaConfig.BATCH_LISTENER)
    public void onMessageLike(List<KafkaMessage<?>> messages,  Acknowledgment ack) {
        for(KafkaMessage<?> msg : messages) {
            try {
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
            } catch (Exception e) {
                log.error("Failed to process message with key {}: {}", e.getMessage(), e);
            }
        }
        ack.acknowledge();
    }

    @KafkaListener(topics = StockMessage.TOPIC, groupId = GROUP_ID, containerFactory = KafkaConfig.BATCH_LISTENER)
    public void onMessageStock(List<KafkaMessage<?>> messages, Acknowledgment ack) {
        for (KafkaMessage<?> msg : messages) {
            try {
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
            } catch (Exception e) {
                log.error("Failed to process message with key {}: {}", msg.getEventId(), e.getMessage(), e);
            }
        }

        ack.acknowledge();
    }

    @KafkaListener(topics = ProductMessage.TOPIC, groupId = GROUP_ID, containerFactory = KafkaConfig.BATCH_LISTENER)
    public void onMessageView(List<KafkaMessage<?>> messages, Acknowledgment ack) {
        for (KafkaMessage<?> msg : messages) {
            try {
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
            } catch (Exception e) {
                log.error("Failed to process message with key {}: {}", msg.getEventId(), e.getMessage(), e);
            }
        }
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

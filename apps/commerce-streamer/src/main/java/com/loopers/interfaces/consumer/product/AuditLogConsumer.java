package com.loopers.interfaces.consumer.product;

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

    @KafkaListener(topics = LikeMessage.TOPIC, groupId = GROUP_ID)
    public void onMessageLike(KafkaMessage<?> msg, Acknowledgment ack) {
        template.consume(GROUP_ID, msg, () ->
                auditLogService.createAuditLog(AuditLogCommand.Save.of(
                        LikeMessage.TOPIC,
                        msg.getEventId(),
                        msg.getVersion(),
                        msg.getPublishedAt(),
                        msg.getEventType(),
                        msg.getPayload().toString()
                ))
        );
        ack.acknowledge();
    }

    @KafkaListener(topics = StockMessage.TOPIC, groupId = GROUP_ID)
    public void onMessageStock(KafkaMessage<?> msg, Acknowledgment ack) {
        template.consume(GROUP_ID, msg, () ->
                auditLogService.createAuditLog(AuditLogCommand.Save.of(
                        StockMessage.TOPIC,
                        msg.getEventId(),
                        msg.getVersion(),
                        msg.getPublishedAt(),
                        msg.getEventType(),
                        msg.getPayload().toString()
                ))
        );

        ack.acknowledge();
    }

    @KafkaListener(topics = ProductMessage.TOPIC, groupId = GROUP_ID)
    public void onMessageView(KafkaMessage<?> msg, Acknowledgment ack) {
        template.consume(GROUP_ID, msg, () ->
                auditLogService.createAuditLog(AuditLogCommand.Save.of(
                        ProductMessage.TOPIC,
                        msg.getEventId(),
                        msg.getVersion(),
                        msg.getPublishedAt(),
                        msg.getEventType(),
                        msg.getPayload().toString()
                ))
        );
        ack.acknowledge();
    }
}

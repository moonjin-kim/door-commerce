package com.loopers.interfaces.consumer.product;

import com.loopers.applicaiton.product.ProductCacheFacade;
import com.loopers.config.kafka.KafkaConfig;
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
public class ProductCacheConsumer {
    private final String GROUP_ID = "product-cache";
    private final ProductCacheFacade productCacheFacade;
    private final ConsumeTemplate template;

    @KafkaListener(topics = LikeMessage.TOPIC, groupId = GROUP_ID, containerFactory = KafkaConfig.BATCH_LISTENER)
    public void onMessageLike(List<KafkaMessage<?>> messages, Acknowledgment ack) {
        for (KafkaMessage<?> msg : messages) {
            try {
                switch (msg.getEventType()) {
                    case LikeMessage.V1.Type.CHANGED -> {
                        LikeMessage.V1.Changed payload = (LikeMessage.V1.Changed) msg.getPayload();
                        template.consume(GROUP_ID, msg, () ->
                                productCacheFacade.removeCache(payload.productId())
                        );
                        ack.acknowledge();
                    }
                    default -> {
                        template.consume(GROUP_ID, msg, () -> log.info("Not Support Type: {}", msg.getPayload()));
                        ack.acknowledge();
                    }
                }
            } catch (Exception e) {
                log.error("Failed to process message with key {}: {}", msg.getEventId(), e.getMessage(), e);
            }
        }
    }

    @KafkaListener(topics = StockMessage.TOPIC, groupId = GROUP_ID, containerFactory = KafkaConfig.BATCH_LISTENER)
    public void onMessageStock(List<KafkaMessage<?>> messages, Acknowledgment ack) {
        for (KafkaMessage<?> msg : messages) {
            try {
                switch (msg.getEventType()) {
                    case StockMessage.V1.Type.SOLD_OUT -> {
                        StockMessage.V1.SOLD_OUT payload = (StockMessage.V1.SOLD_OUT) msg.getPayload();
                        template.consume(GROUP_ID, msg, () ->
                                productCacheFacade.removeCache(payload.productId())
                        );

                        ack.acknowledge();
                    }
                    default -> {
                        template.consume(GROUP_ID, msg, () -> log.info("Not Support Type: {}", msg.getPayload()));
                        ack.acknowledge();
                    }
                }
            } catch (Exception e) {
                log.error("Failed to process message with key {}: {}", msg.getEventId(), e.getMessage(), e);
            }
        }
    }
}

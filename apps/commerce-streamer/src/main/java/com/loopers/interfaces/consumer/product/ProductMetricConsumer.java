package com.loopers.interfaces.consumer.product;

import com.loopers.applicaiton.product.ProductMetricFacade;
import com.loopers.config.kafka.KafkaConfig;
import com.loopers.support.event.ConsumeTemplate;
import com.loopers.support.event.KafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductMetricConsumer {
    private final String GROUP_ID = "product-metrics";
    private final ProductMetricFacade productMetricFacade;
    private final ConsumeTemplate template;

    @KafkaListener(topics = LikeMessage.TOPIC, groupId = GROUP_ID, containerFactory = KafkaConfig.BATCH_LISTENER)
    public void onMessageLike(List<KafkaMessage<?>> messages, Acknowledgment ack) {
        List<LikeMessage.V1.Changed> likePayloadsForBatch = new ArrayList<>();

        // 모든 메시지를 단 한 번 순회
        messages.forEach(msg -> {
            if (msg.getEventType().equals(LikeMessage.V1.Type.CHANGED)) {
                LikeMessage.V1.Changed payload = (LikeMessage.V1.Changed) msg.getPayload();
                likePayloadsForBatch.add(payload);
            } else {
                template.consume(GROUP_ID, msg, () -> log.info("Not Support Type: {}", msg.getPayload()));
            }
        });

        // 순회가 끝난 후, 모아둔 '좋아요 변경' 메시지들을 한 번에 처리
        if (!likePayloadsForBatch.isEmpty()) {
            productMetricFacade.updateLikeCounts(likePayloadsForBatch, LocalDate.now());
        }

        messages.forEach(msg -> template.consume(GROUP_ID, msg, () -> {}));
        ack.acknowledge();
    }

    @KafkaListener(topics = StockMessage.TOPIC, groupId = GROUP_ID, containerFactory = KafkaConfig.BATCH_LISTENER)
    public void onMessageStock(List<KafkaMessage<?>> messages, Acknowledgment ack) {
        List<StockMessage.V1.Changed> likePayloadsForBatch = new ArrayList<>();

        // 모든 메시지를 단 한 번 순회
        messages.forEach(msg -> {
            if (msg.getEventType().equals(StockMessage.V1.Type.CHANGED)) {
                StockMessage.V1.Changed payload = (StockMessage.V1.Changed) msg.getPayload();
                likePayloadsForBatch.add(payload);
            } else {
                template.consume(GROUP_ID, msg, () -> log.info("Not Support Type: {}", msg.getPayload()));
            }
        });

        // 순회가 끝난 후, 모아둔 '좋아요 변경' 메시지들을 한 번에 처리
        if (!likePayloadsForBatch.isEmpty()) {
            productMetricFacade.updateOrderCounts(likePayloadsForBatch, LocalDate.now());
        }

        messages.forEach(msg -> template.consume(GROUP_ID, msg, () -> {}));
        ack.acknowledge();
    }

    @KafkaListener(topics = ProductMessage.TOPIC, groupId = GROUP_ID, containerFactory = KafkaConfig.BATCH_LISTENER)
    public void onMessageView(List<KafkaMessage<?>> messages, Acknowledgment ack) {
        List<ProductMessage.V1.Viewed> payloads = new ArrayList<>();

        // 모든 메시지를 단 한 번 순회
        messages.forEach(msg -> {
            if (msg.getEventType().equals(ProductMessage.V1.Type.VIEW)) {
                ProductMessage.V1.Viewed payload = (ProductMessage.V1.Viewed) msg.getPayload();
                payloads.add(payload);
            } else {
                template.consume(GROUP_ID, msg, () -> log.info("Not Support Type: {}", msg.getPayload()));
            }
        });

        // 순회가 끝난 후, 모아둔 '좋아요 변경' 메시지들을 한 번에 처리
        if (!payloads.isEmpty()) {
            productMetricFacade.updateViewCounts(payloads, LocalDate.now());
        }

        messages.forEach(msg -> template.consume(GROUP_ID, msg, () -> {}));
        ack.acknowledge();
    }

}

package com.loopers.interfaces.consumer.product;

import com.loopers.applicaiton.product.ProductMetricFacade;
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
public class ProductMetricConsumer {
    private final String GROUP_ID = "product-metrics";
    private final ProductMetricFacade productMetricFacade;
    private final ConsumeTemplate template;

    @KafkaListener(topics = LikeMessage.TOPIC, groupId = GROUP_ID)
    public void onMessageLike(KafkaMessage<?> msg, Acknowledgment ack) {
        switch (msg.getEventType()) {
            case LikeMessage.V1.Type.CHANGED -> {
                LikeMessage.V1.Changed payload = (LikeMessage.V1.Changed) msg.getPayload();
                template.consume(GROUP_ID, msg, () ->
                        productMetricFacade.updateLikeCount(payload, msg.getPublishedAt())
                );
                ack.acknowledge();
            }
            default -> {
                //todo: dlq에 보내기
                throw new IllegalArgumentException("Unknown event type: " + msg.getEventType());
            }
        }
        ack.acknowledge();
    }

    @KafkaListener(topics = StockMessage.TOPIC, groupId = GROUP_ID)
    public void onMessageStock(KafkaMessage<?> msg, Acknowledgment ack) {
        switch (msg.getEventType()) {
            case StockMessage.V1.Type.CHANGED -> {
                StockMessage.V1.Changed payload = (StockMessage.V1.Changed) msg.getPayload();
                template.consume(GROUP_ID, msg, () ->
                        productMetricFacade.updateOrderQuantity(payload, msg.getPublishedAt())
                );
            }
            case StockMessage.V1.Type.OUT -> {
                template.consume(GROUP_ID, msg, () -> log.info("Received out: {}", msg.getPayload()));

                ack.acknowledge();
            }
            default -> {
                //todo: dlq에 보내기
                throw new IllegalArgumentException("Unknown event type: " + msg.getEventType());
            }
        }
    }

    @KafkaListener(topics = ProductMessage.TOPIC, groupId = GROUP_ID)
    public void onMessageView(KafkaMessage<?> msg, Acknowledgment ack) {
        switch (msg.getEventType()) {
            case ProductMessage.V1.Type.VIEW -> {
                ProductMessage.V1.Viewed payload = (ProductMessage.V1.Viewed) msg.getPayload();
                template.consume(GROUP_ID, msg, () ->
                        productMetricFacade.updateViewCount(payload, msg.getPublishedAt())
                );
                ack.acknowledge();
            } default -> {
                //todo: dlq에 보내기
                throw new IllegalArgumentException("Unknown event type: " + msg.getEventType());
            }
        }
    }

}

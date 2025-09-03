package com.loopers.interfaces.consumer.product;

import com.loopers.applicaiton.product.ProductMetricFacade;
import com.loopers.support.KafkaMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductConsumer {
    private final String GROUP_ID = "product-metrics";
    private final ProductMetricFacade productMetricFacade;

    @KafkaListener(topics = LikeMessage.TOPIC.CHANGED, groupId = GROUP_ID)
    public void onMessageLike(KafkaMessage<?> msg) {
        switch (msg.getEventType()) {
            case LikeMessage.V1.Type.CHANGED -> {
                LikeMessage.V1.Changed payload = (LikeMessage.V1.Changed) msg.getPayload();
                productMetricFacade.updateLikeCount(payload, msg.getPublishedAt(), msg.getEventId(), GROUP_ID);
            }
            default -> {
                //todo: dlq에 보내기
                throw new IllegalArgumentException("Unknown event type: " + msg.getEventType());
            }
        }
    }

    @KafkaListener(topics = StockMessage.TOPIC.CHANGE, groupId = GROUP_ID)
    public void onMessageStock(KafkaMessage<?> msg) {
        switch (msg.getEventType()) {
            case StockMessage.V1.Type.CHANGED -> {
                StockMessage.V1.Changed payload = (StockMessage.V1.Changed) msg.getPayload();
                productMetricFacade.updateOrderQuantity(payload, msg.getPublishedAt(), msg.getEventId(), GROUP_ID);
            } default -> {
                //todo: dlq에 보내기
                throw new IllegalArgumentException("Unknown event type: " + msg.getEventType());
            }
        }
    }

}

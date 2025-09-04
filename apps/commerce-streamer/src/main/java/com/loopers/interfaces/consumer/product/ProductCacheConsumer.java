package com.loopers.interfaces.consumer.product;

import com.loopers.applicaiton.product.ProductCacheFacade;
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
public class ProductCacheConsumer {
    private final String GROUP_ID = "product-cache";
    private final ProductCacheFacade productCacheFacade;
    private final ConsumeTemplate template;

    @KafkaListener(topics = LikeMessage.TOPIC, groupId = GROUP_ID)
    public void onMessageLike(KafkaMessage<?> msg, Acknowledgment ack) {
        switch (msg.getEventType()) {
            case LikeMessage.V1.Type.CHANGED -> {
                LikeMessage.V1.Changed payload = (LikeMessage.V1.Changed) msg.getPayload();
                template.consume(GROUP_ID, msg, () ->
                        productCacheFacade.removeCache(payload.productId())
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

                template.consume(GROUP_ID, msg, () -> log.info("Received change: {}", msg.getPayload()));
            }
            case StockMessage.V1.Type.OUT -> {
                StockMessage.V1.Out payload = (StockMessage.V1.Out) msg.getPayload();
                template.consume(GROUP_ID, msg, () ->
                        productCacheFacade.removeCache(payload.productId())
                );

                ack.acknowledge();
            }
            default -> {
                //todo: dlq에 보내기
                throw new IllegalArgumentException("Unknown event type: " + msg.getEventType());
            }
        }
    }
}

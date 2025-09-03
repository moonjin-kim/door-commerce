package com.loopers.applicaiton.product;

import com.loopers.domain.product.ProductMetricService;
import com.loopers.interfaces.consumer.product.LikeMessage;
import com.loopers.support.KafkaMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ProductMetricFacade {
    private final ProductMetricService productMetricService;

    public void updateLikeCount(LikeMessage.V1.Changed message, LocalDateTime publishedAt, String eventId) {
        productMetricService.updateLikeCount(message.toCommand(publishedAt.toLocalDate()));
    }
}

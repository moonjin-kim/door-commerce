package com.loopers.applicaiton.product;

import com.loopers.domain.event_hendler.EventHandlerService;
import com.loopers.domain.product.ProductMetricService;
import com.loopers.interfaces.consumer.product.LikeMessage;
import com.loopers.interfaces.consumer.product.ProductMessage;
import com.loopers.interfaces.consumer.product.StockMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ProductMetricFacade {
    private final ProductMetricService productMetricService;
    private final EventHandlerService eventHandlerService;

    public void updateLikeCount(LikeMessage.V1.Changed message, LocalDateTime publishedAt, String eventId, String groupId) {
        if (eventHandlerService.existEventBy(eventId, groupId)) {
            return;
        }
        eventHandlerService.save(eventId, groupId);
        productMetricService.updateLikeCount(message.toCommand(publishedAt.toLocalDate()));
    }

    public void updateOrderQuantity(StockMessage.V1.Changed message, LocalDateTime publishedAt, String eventId, String groupId) {
        if (eventHandlerService.existEventBy(eventId, groupId)) {
            return;
        }
        eventHandlerService.save(eventId, groupId);
        productMetricService.updateOrderQuantity(message.toCommand(publishedAt.toLocalDate()));
    }

    public void updateViewCount(ProductMessage.V1.Viewed message, LocalDateTime publishedAt, String eventId, String groupId) {
        if (eventHandlerService.existEventBy(eventId, groupId)) {
            return;
        }
        eventHandlerService.save(eventId, groupId);
        productMetricService.updateViewCount(message.toCommand(publishedAt.toLocalDate()));
    }
}

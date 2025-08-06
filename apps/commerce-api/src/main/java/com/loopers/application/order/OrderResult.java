package com.loopers.application.order;

import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderInfo;

import java.util.List;

public class OrderResult {
    public record Order(
            Long id,
            Long userId,
            List<OrderItem> items,
            long totalPrice,
            String status
    ) {
        public static OrderResult.Order from(com.loopers.domain.order.Order order) {
            return new Order(
                    order.getId(),
                    order.getUserId(),
                    order.getOrderItems().stream()
                            .map(item -> new OrderItem(
                                    item.getProductId(),
                                    item.getName(),
                                    item.getProductPrice().value(),
                                    item.getQuantity()))
                            .toList(),
                    order.getTotalAmount().value(),
                    order.getStatus().name()
            );
        }
    }

    public record OrderItem(
            Long productId,
            String productName,
            long price,
            int quantity
    ) {
    }
}

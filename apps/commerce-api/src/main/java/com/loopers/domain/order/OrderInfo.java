package com.loopers.domain.order;

import java.time.LocalDateTime;
import java.util.List;

public class OrderInfo {
    public record OrderDto(
            Long orderId,
            Long userId,
            List<OrderItemDto> orderItemDtos,
            Long totalPrice,
            Long pointUsed,
            LocalDateTime orderDate,
            OrderStatus status
    ) {
        public static OrderDto of(Order order) {
            List<OrderItemDto> orderItemDtos = order.getOrderItems().stream()
                    .map(OrderItemDto::of)
                    .toList();

            return new OrderDto(order.getId(), order.getUserId(), orderItemDtos, order.getTotalPrice(), order.getPointUsed(), order.getOrderDate(), order.getStatus());
        }
    }

    public record OrderItemDto(
            Long productId,
            String name,
            long price,
            int quantity
    ) {
        public static OrderItemDto of(OrderItem orderItem) {
            return new OrderItemDto(orderItem.getProductId(), orderItem.getName(), orderItem.getProductPrice(), orderItem.getQuantity());
        }
    }

}

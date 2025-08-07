package com.loopers.domain.order;

import java.time.LocalDateTime;
import java.util.List;

public class OrderInfo {
    public record OrderDto(
            Long orderId,
            Long userId,
            List<OrderItemDto> orderItemDtos,
            Long totalPrice,
            LocalDateTime orderDate,
            OrderStatus status
    ) {
        public static OrderDto from(Order order) {
            List<OrderItemDto> orderItemDtos = order.getOrderItems().stream()
                    .map(OrderItemDto::from)
                    .toList();

            return new OrderDto(
                    order.getId(),
                    order.getUserId(),
                    orderItemDtos,
                    order.getTotalAmount().value().longValue(),
                    order.getOrderDate(),
                    order.getStatus()
            );
        }
    }

    public record OrderItemDto(
            Long productId,
            String name,
            long price,
            int quantity
    ) {
        public static OrderItemDto from(OrderItem orderItem) {
            return new OrderItemDto(orderItem.getProductId(), orderItem.getName(), orderItem.getProductPrice().value().longValue(), orderItem.getQuantity());
        }
    }

}

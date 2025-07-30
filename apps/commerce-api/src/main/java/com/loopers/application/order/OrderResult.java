package com.loopers.application.order;

import com.loopers.domain.order.OrderInfo;

import java.util.List;

public class OrderResult {
    public record Order(
            Long id,
            Long userId,
            List<OrderItem> items,
            long totalPrice,
            long pointUsed,
            String status
    ) {
        public static Order of(OrderInfo.OrderDto orderInfo) {
            return new Order(
                    orderInfo.orderId(),
                    orderInfo.userId(),
                    orderInfo.orderItemDtos().stream()
                            .map(item -> new OrderItem(
                                    item.productId(),
                                    item.name(),
                                    item.price(),
                                    item.quantity()))
                            .toList(),
                    orderInfo.totalPrice(),
                    orderInfo.pointUsed(),
                    orderInfo.status().name()
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

package com.loopers.interfaces.api.order;

import com.loopers.application.order.OrderCriteria;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OrderV1Request {
    public record Order(
            List<OrderV1Request.OrderItem> items,
            Long couponId
    ){
        public OrderCriteria.Order toCommand(Long userId) {
            List<OrderCriteria.OrderItem> orderItems = items.stream().map(OrderItem::toCommand).collect(Collectors.toList());
            return OrderCriteria.Order.of(userId, orderItems, couponId);
        }
    }

    public record OrderItem(
            Long productId,
            Integer quantity
    ) {
        public OrderCriteria.OrderItem toCommand() {
            return new OrderCriteria.OrderItem(productId, quantity);
        }
    }
}


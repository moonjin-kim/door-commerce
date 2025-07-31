package com.loopers.application.order;

import com.loopers.domain.order.OrderCommand;

import java.util.List;

public class OrderCriteria {
    public record Order(
            Long userId,
            List<OrderItem> items
    ) {}

    public record OrderItem(
            Long productId,
            int quantity
    ) { }

    public record GetOrdersBy(
            Long userId
    ){
        public OrderCommand.GetOrdersBy toCommand() {
            return OrderCommand.GetOrdersBy.of(userId);
        }
    }
}

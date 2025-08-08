package com.loopers.application.order;

import com.loopers.domain.order.OrderCommand;

import java.util.List;

public class OrderCriteria {
    public record Order(
            Long userId,
            List<OrderItem> items,
            Long couponId
    ) {
        public static OrderCriteria.Order of(Long userId, List<OrderCriteria.OrderItem> items, Long couponId) {
            return new OrderCriteria.Order(userId, items, couponId);
        }
    }

    public record OrderItem(
            Long productId,
            int quantity
    ) { }

    public record GetOrdersBy(
            Long userId
    ){
        public static OrderCriteria.GetOrdersBy of(Long userId) {
            return new OrderCriteria.GetOrdersBy(userId);
        }

        public OrderCommand.GetOrdersBy toCommand() {
            return OrderCommand.GetOrdersBy.of(userId);
        }
    }
}

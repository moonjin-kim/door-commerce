package com.loopers.domain.order;

import java.util.List;

public class OrderCommand {
    public record Order(
            Long userId,
            List<OrderItem> orderItems
    ) {
        public static Order of(Long userId, List<OrderItem> orderItems) {
            return new Order(userId, orderItems);
        }
    }

    public record OrderItem(Long productId, String name, long price, int quantity) {
        public static OrderItem of(Long productId, String name, long price, int quantity) {
            return new OrderItem(productId, name, price, quantity);
        }
    }
}

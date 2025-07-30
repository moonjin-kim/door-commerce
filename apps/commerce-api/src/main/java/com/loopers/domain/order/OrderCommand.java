package com.loopers.domain.order;

import java.util.List;

public class OrderCommand {
    public record Order(
            Long userId,
            List<OrderItem> orderItems
    ) {

    }

    public record OrderItem(Long productId, String name, int price, int quantity) {
        public static OrderItem of(Long productId, String name, int price, int quantity) {
            return new OrderItem(productId, name, price, quantity);
        }
    }
}

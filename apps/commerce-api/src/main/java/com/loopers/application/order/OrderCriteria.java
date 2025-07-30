package com.loopers.application.order;

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
}

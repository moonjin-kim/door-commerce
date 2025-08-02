package com.loopers.domain.order;

import com.loopers.infrastructure.order.OrderParams;
import com.loopers.infrastructure.product.ProductParams;

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

    public record GetBy(Long orderId, Long userId) {
        public static GetBy of(Long orderId, Long userId) {
            return new GetBy(orderId, userId);
        }
    }

    public record GetOrdersBy(Long userId) {
        public static GetOrdersBy of(Long userId) {
            return new GetOrdersBy(userId);
        }

        public OrderParams.GetOrdersBy toParams() {
            return OrderParams.GetOrdersBy.of(userId);
        }
    }
}

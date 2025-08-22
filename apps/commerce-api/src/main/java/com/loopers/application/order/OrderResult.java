package com.loopers.application.order;

import java.util.List;

public class OrderResult {
    public record Order(
            Long id,
            String orderId,
            Long userId,
            List<OrderItem> items,
            Long userCouponId,
            long totalPrice,
            long couponDiscount,
            long finalAmount,
            String status
    ) {
        public static OrderResult.Order from(com.loopers.domain.order.Order order) {
            return new Order(
                    order.getId(),
                    order.getOrderId(),
                    order.getUserId(),
                    order.getOrderItems().stream()
                            .map(item -> new OrderItem(
                                    item.getProductId(),
                                    item.getName(),
                                    item.getProductPrice().value().longValue(),
                                    item.getQuantity()))
                            .toList(),
                    order.getUserCouponId(),
                    order.getTotalAmount().value().longValue(),
                    order.getCouponDiscountAmount().value().longValue(),
                    order.getFinalAmount().value().longValue(),
                    order.getStatus().name()
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

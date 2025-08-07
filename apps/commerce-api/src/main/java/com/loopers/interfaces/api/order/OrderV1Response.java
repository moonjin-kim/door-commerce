package com.loopers.interfaces.api.order;

import com.loopers.application.order.OrderResult;
import com.loopers.domain.order.OrderInfo;

import java.util.List;

public class OrderV1Response {
    public record Order(
            Long id,
            Long userId,
            List<OrderV1Response.OrderItem> items,
            Long userCouponId,
            long totalPrice,
            long couponDiscount,
            long finalAmount,
            String status
    ) {
        public static OrderV1Response.Order from(OrderResult.Order orderResult) {
            return new OrderV1Response.Order(
                    orderResult.id(),
                    orderResult.userId(),
                    orderResult.items().stream()
                            .map(item -> new OrderV1Response.OrderItem(
                                    item.productId(),
                                    item.productName(),
                                    item.price(),
                                    item.quantity()))
                            .toList(),
                    orderResult.userCouponId(),
                    orderResult.totalPrice(),
                    orderResult.couponDiscount(),
                    orderResult.finalAmount(),
                    orderResult.status()
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

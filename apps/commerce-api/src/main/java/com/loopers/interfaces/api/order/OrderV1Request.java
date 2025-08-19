package com.loopers.interfaces.api.order;

import com.loopers.application.order.OrderCriteria;
import com.loopers.application.payment.PaymentMethodType;
import com.loopers.application.payment.pg.CardType;

import java.util.List;
import java.util.stream.Collectors;

public class OrderV1Request {
    public record Order(
            List<OrderV1Request.OrderItem> items,
            Long couponId,
            PaymentMethodType paymentMethod,
            CardType cardType,
            String cardNumber
    ){
        public OrderCriteria.Order toCriteria(Long userId) {
            List<OrderCriteria.OrderItem> orderItems = items.stream().map(OrderItem::toCriteria).collect(Collectors.toList());
            return OrderCriteria.Order.of(
                    userId,
                    orderItems,
                    couponId,
                    paymentMethod,
                    cardType,
                    cardNumber
            );
        }
    }

    public record Callback(
            String transactionKey,
            String orderId,
            CardType cardType,
            String cardNo,
            String amount,
            String transactionStatus,
            String reason
    ) {
        public OrderCriteria.Callback toCriteria() {
            return OrderCriteria.Callback.of(
                    transactionKey,
                    orderId,
                    cardType,
                    cardNo,
                    amount,
                    transactionStatus,
                    reason
            );
        }
    }

    public record OrderItem(
            Long productId,
            Integer quantity
    ) {
        public OrderCriteria.OrderItem toCriteria() {
            return new OrderCriteria.OrderItem(productId, quantity);
        }
    }
}


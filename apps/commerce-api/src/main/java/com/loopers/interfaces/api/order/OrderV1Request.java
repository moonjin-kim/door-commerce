package com.loopers.interfaces.api.order;

import com.loopers.application.order.OrderCriteria;
import com.loopers.application.payment.PaymentMethodType;
import com.loopers.application.payment.pg.CardType;
import com.loopers.domain.payment.PaymentCommand;

import java.util.Arrays;
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
        public OrderCriteria.Order toCommand(Long userId) {
            List<OrderCriteria.OrderItem> orderItems = items.stream().map(OrderItem::toCommand).collect(Collectors.toList());
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

    public record OrderItem(
            Long productId,
            Integer quantity
    ) {
        public OrderCriteria.OrderItem toCommand() {
            return new OrderCriteria.OrderItem(productId, quantity);
        }
    }
}


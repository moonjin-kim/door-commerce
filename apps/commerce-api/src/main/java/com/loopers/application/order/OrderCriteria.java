package com.loopers.application.order;

import com.loopers.application.order.payment.PaymentMethodType;
import com.loopers.domain.pg.CardType;
import com.loopers.domain.order.OrderCommand;

import java.util.List;

public class OrderCriteria {
    public record Order(
            Long userId,
            List<OrderItem> items,
            Long couponId,
            PaymentMethodType paymentMethodType,
            CardType cardType,
            String cardNumber
    ) {
        public static OrderCriteria.Order of(
                Long userId,
                List<OrderCriteria.OrderItem> items,
                Long couponId,
                PaymentMethodType paymentMethodType,
                CardType cardType,
                String cardNumber
        ) {
            return new OrderCriteria.Order(userId, items, couponId, paymentMethodType, cardType, cardNumber);
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
        public static OrderCriteria.Callback of(
                String transactionKey,
                String orderId,
                CardType cardType,
                String cardNo,
                String amount,
                String transactionStatus,
                String reason
        ) {
            return new OrderCriteria.Callback(transactionKey, orderId, cardType, cardNo, amount, transactionStatus, reason);
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

    public enum PaymentMethod {
        POINT,
        CARD
    }
}

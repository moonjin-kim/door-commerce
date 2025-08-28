package com.loopers.domain.order;

import com.loopers.application.payment.PaymentMethodType;
import com.loopers.domain.pg.CardType;

public class OrderEvent {
    public record RequestPayment(
        String orderId,
        Long userId,
        Long amount,
        String paymentMethodType,
        String cardType,
        String cardNumber
    ) {
        static public RequestPayment of(String orderId, Long userId, Long amount, PaymentMethodType methodType, CardType cardType, String cardNumber) {
            return new RequestPayment(orderId, userId, amount, methodType.name(), cardType != null ? cardType.name() : null, cardNumber);
        }
    }

    public record ConsumeStockCommand(
            Long productId,
            int quantity
    ) {
        static public ConsumeStockCommand of(Long productId,
                                             int quantity) {
            return new ConsumeStockCommand(productId, quantity);
        }
    }

    public record RollbackStockCommand(
            Long productId,
            int quantity
    ) {
        static public RollbackStockCommand of(Long productId,
                                                 int quantity) {
            return new RollbackStockCommand(productId, quantity);
        }
    }

    public record Complete(
        String orderId,
        Long amount
    ) {
        static public Complete of(String orderId, Long amount) {
            return new Complete(orderId, amount);
        }
    }

    public record Cancel(
        String orderId,
        Long amount
    ) {
        static public Cancel of(String orderId, Long amount) {
            return new Cancel(orderId, amount);
        }
    }
}

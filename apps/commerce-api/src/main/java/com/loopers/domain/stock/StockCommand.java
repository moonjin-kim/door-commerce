package com.loopers.domain.stock;

import com.loopers.domain.order.OrderCommand;

public class StockCommand {
    public record Create(
            Long productId,
            int quantity
    ) {
        public static StockCommand.Create of(Long productId, int quantity) {
            return new StockCommand.Create(productId, quantity);
        }
    }


    public record Decrease(
            Long productId,
            int quantity
    ) {
        public static Decrease of(Long productId, int quantity) {
            return new Decrease(productId, quantity);
        }

        public static Decrease from(OrderCommand.OrderItem orderItem) {
            return new Decrease(orderItem.productId(), orderItem.quantity());
        }
    }

    public record Increase(
            Long productId,
            int quantity
    ) {
        public static Increase of(Long productId, int quantity) {
            return new Increase(productId, quantity);
        }

        public static Increase from(OrderCommand.OrderItem orderItem) {
            return new Increase(orderItem.productId(), orderItem.quantity());
        }
    }

}

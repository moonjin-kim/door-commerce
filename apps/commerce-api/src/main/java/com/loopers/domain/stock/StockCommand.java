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
        public static StockCommand.Decrease of(Long productId, int quantity) {
            return new StockCommand.Decrease(productId, quantity);
        }

        public static StockCommand.Decrease from(OrderCommand.OrderItem orderItem) {
            return new StockCommand.Decrease(orderItem.productId(), orderItem.quantity());
        }
    }

}

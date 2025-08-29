package com.loopers.domain.stock;

import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderEvent;

public class StockCommand {
    public record Create(
            Long productId,
            int quantity
    ) {
        public static StockCommand.Create of(Long productId, int quantity) {
            return new StockCommand.Create(productId, quantity);
        }
    }


    public record Consume(
            Long productId,
            int quantity
    ) {
        public static Consume of(Long productId, int quantity) {
            return new Consume(productId, quantity);
        }

        public static Consume from(OrderCommand.OrderItem orderItem) {
            return new Consume(orderItem.productId(), orderItem.quantity());
        }

        public static Consume from(OrderEvent.ConsumeStockCommand event) {
            return new Consume(event.productId(), event.quantity());
        }
    }

    public record Rollback(
            Long productId,
            int quantity
    ) {
        public static Rollback of(Long productId, int quantity) {
            return new Rollback(productId, quantity);
        }

        public static Rollback from(OrderCommand.OrderItem orderItem) {
            return new Rollback(orderItem.productId(), orderItem.quantity());
        }

        public static Rollback from(OrderEvent.RollbackStockCommand event) {
            return new Rollback(event.productId(), event.quantity());
        }
    }

}

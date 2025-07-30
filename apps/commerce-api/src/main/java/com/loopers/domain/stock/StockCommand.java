package com.loopers.domain.stock;

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
    }

    public record DecreaseItem(
            Long productId,
            int quantity
    ) {

    }

}

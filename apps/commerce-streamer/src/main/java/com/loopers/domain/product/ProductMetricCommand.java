package com.loopers.domain.product;

import java.time.LocalDate;

public class ProductMetricCommand {
    public record Create(Long productId, LocalDate date) {
        public static Create of(Long productId, LocalDate date) {
            return new Create(productId, date);
        }
    }

    public record LikeChange(Long productId, LocalDate date, Long delta) {
        public static LikeChange of(Long productId, LocalDate date, Long delta) {
            return new LikeChange(productId, date, delta);
        }
    }

    public record StockChange(Long productId, LocalDate date, Long quantity) {
        public static StockChange of(Long productId, LocalDate date, Long quantity) {
            return new StockChange(productId, date, quantity);
        }
    }

    public record ViewChange(Long productId, LocalDate date) {
        public static ViewChange of(Long productId, LocalDate date) {
            return new ViewChange(productId, date);
        }
    }
}

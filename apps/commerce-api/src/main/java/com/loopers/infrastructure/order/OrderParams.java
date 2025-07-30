package com.loopers.infrastructure.order;

public class OrderParams {
    public record GetOrdersBy(Long userId) {
        public static GetOrdersBy of(Long userId) {
            return new GetOrdersBy(userId);
        }
    }
}

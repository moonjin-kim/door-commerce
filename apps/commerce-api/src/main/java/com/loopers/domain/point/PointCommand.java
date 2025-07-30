package com.loopers.domain.point;

public class PointCommand {
    public record Charge(
            Long userId,
            int amount
    ) {
        public static Charge of(Long userId, int amount) {
            return new Charge(userId, amount);
        }
    }

    public record Using(
            Long userId,
            Long orderId,
            int amount
    ) {
        public static Using of(Long userId, long orderId,int amount) {
            return new Using(userId, orderId, amount);
        }
    }
}

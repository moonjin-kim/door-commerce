package com.loopers.domain.point;

public class PointCommand {
    public record Charge(
            Long userId,
            long amount
    ) {
        public static Charge of(Long userId, long amount) {
            return new Charge(userId, amount);
        }
    }

    public record Using(
            Long userId,
            Long orderId,
            long amount
    ) {
        public static Using of(Long userId, long orderId,long amount) {
            return new Using(userId, orderId, amount);
        }
    }
}

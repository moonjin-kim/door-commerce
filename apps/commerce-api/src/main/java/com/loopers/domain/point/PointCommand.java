package com.loopers.domain.point;

public class PointCommand {
    public record Charge(
            Long userId,
            Long amount
    ) {
        public static Charge of(Long userId, Long amount) {
            return new Charge(userId, amount);
        }
    }

    public record Using(
            Long userId,
            Long orderId,
            Long amount
    ) {
        public static Using of(Long userId, Long orderId,Long amount) {
            return new Using(userId, orderId, amount);
        }
    }
}

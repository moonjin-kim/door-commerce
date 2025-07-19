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
}

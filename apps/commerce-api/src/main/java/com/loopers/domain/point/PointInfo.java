package com.loopers.domain.point;


public record PointInfo(
        Long userId,
        long balance
) {
    public static PointInfo of(Point point) {
        return new PointInfo(
                point.getUserId(),
                point.balance().value()
        );
    }
}

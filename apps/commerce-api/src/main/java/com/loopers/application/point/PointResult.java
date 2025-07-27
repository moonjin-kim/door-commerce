package com.loopers.application.point;

import com.loopers.domain.point.Point;

public record PointResult(Long userId, int balance) {
    public static PointResult from(Point point) {
        return new PointResult(
                point.getUserId(),
                point.getBalance()
        );
    }
}

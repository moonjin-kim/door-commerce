package com.loopers.application.point;

import com.loopers.domain.point.Point;

public record PointResult(Long userId, long balance) {
    public static PointResult from(Point info) {
        return new PointResult(
                info.getUserId(),
                info.getBalance().longValue()
        );
    }
}

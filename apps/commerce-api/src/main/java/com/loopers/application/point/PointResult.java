package com.loopers.application.point;

import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointInfo;

public record PointResult(Long userId, long balance) {
    public static PointResult from(PointInfo info) {
        return new PointResult(
                info.userId(),
                info.balance()
        );
    }
}

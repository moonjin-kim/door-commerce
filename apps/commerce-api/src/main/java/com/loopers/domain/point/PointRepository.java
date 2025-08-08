package com.loopers.domain.point;

import com.loopers.domain.user.User;

import java.util.Optional;

public interface PointRepository {
    Point save(Point point);
    Optional<Point> findBy(Long userId);
    Optional<Point> findByForUpdate(Long userId);
}

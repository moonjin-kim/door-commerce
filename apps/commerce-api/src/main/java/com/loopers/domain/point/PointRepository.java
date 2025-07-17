package com.loopers.domain.point;

import com.loopers.domain.user.User;

import java.util.Optional;

public interface PointRepository {
    Optional<Point> findLastByUser(User user);
}

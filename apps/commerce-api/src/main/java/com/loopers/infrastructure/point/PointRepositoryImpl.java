package com.loopers.infrastructure.point;

import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointRepository;
import com.loopers.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class PointRepositoryImpl implements PointRepository {
    private final PointJpaRepository pointJpaRepository;

    @Override
    public Point save(Point point) {
        return pointJpaRepository.save(point);
    }

    @Override
    public Optional<Point> findBy(Long userId) {
        return pointJpaRepository.findByUserId(userId);
    }
}

package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PointService {
    private final PointRepository pointRepository;

    @Transactional
    public Point initPoint(Long userId) {
        if(userId == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "유저 정보가 없습니다.");
        }
        Optional<Point> point = pointRepository.findBy(userId);
        if(point.isPresent()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "포인트 정보가 없습니다.");
        }

        return pointRepository.save(Point.init(userId));
    }

    @Transactional
    public Point chargePoint(Long userId, int amount) {
        // Point Service에서 user를 Null 체크할 이유가 있을까?
        if(userId == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "유저 정보가 없습니다.");
        }
        Point point = pointRepository.findBy(userId).orElseGet(() ->
            initPoint(userId) // 유저가 처음 포인트를 충전하는 경우
        );

        point.charge(amount);

        return point;
    }

    @Transactional
    public Optional<Point> getPoint(Long userId) {
        if(userId == null) {
            return Optional.empty();
        }

        return pointRepository.findBy(userId);
    }

}

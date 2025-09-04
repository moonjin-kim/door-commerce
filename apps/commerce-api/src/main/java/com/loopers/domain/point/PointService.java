package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class PointService {
    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public Point init(Long userId) {
        if(userId == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "유저 정보가 없습니다.");
        }
        Optional<Point> point = pointRepository.findBy(userId);
        if(point.isPresent()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "포인트 정보가 없습니다.");
        }

        return pointRepository.save(Point.create(userId));
    }

    @Transactional
    public Point charge(PointCommand.Charge command) {
        if(command.userId() == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "유저 정보가 없습니다.");
        }

        Point point = pointRepository.findByForUpdate(command.userId()).orElseGet(() ->
            init(command.userId())
        );
        point.charge(command.amount());

        pointHistoryRepository.save(PointHistory.charge(point.getId(), command));

        return point;
    }

    @Transactional
    public Point using(PointCommand.Using command) {
        Point point = pointRepository.findByForUpdate(command.userId()).orElseThrow(() ->
                new CoreException(ErrorType.NOT_FOUND, "포인트 정보가 없습니다.")
        );
        point.use(command.amount());

        pointHistoryRepository.save(PointHistory.use(point.getId(), command));

        return point;
    }

    @Transactional
    public Optional<Point> getBy(Long userId) {
        return pointRepository.findBy(userId);
    }

}

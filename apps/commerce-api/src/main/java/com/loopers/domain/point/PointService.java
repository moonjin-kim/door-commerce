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

        return pointRepository.save(Point.init(userId));
    }

    @Transactional
    public PointInfo charge(PointCommand.Charge command) {
        if(command.userId() == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "유저 정보가 없습니다.");
        }

        Point point = pointRepository.findBy(command.userId()).orElseGet(() ->
            init(command.userId())
        );
        point.charge(command.amount());

        pointHistoryRepository.save(PointHistory.charge(command));

        return PointInfo.of(point);
    }

    @Transactional
    public PointInfo using(PointCommand.Using command) {
        Point point = pointRepository.findBy(command.userId()).orElseThrow(() ->
                new CoreException(ErrorType.NOT_FOUND, "포인트 정보가 없습니다.")
        );
        point.use(command.amount());

        pointHistoryRepository.save(PointHistory.use(command));

        return PointInfo.of(point);
    }

    @Transactional
    public PointInfo get(Long userId) {
        return pointRepository.findBy(userId)
                .map(PointInfo::of)
                .orElseThrow(() ->
                    new CoreException(ErrorType.NOT_FOUND, "포인트 정보가 없습니다.")
                );
    }

}

package com.loopers.domain.point;

import com.loopers.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PointService {
    private final PointRepository pointRepository;

    public Point chargePoint(User user, int amount) {
        // Point Service에서 user를 Null 체크할 이유가 있을까?

        // 포인트 충전한 후에 다시 포인트 내역을 조회해서 잔액을 조회하는 것 보다 그냥 잔액을 초기에 조회해서 컬럼으로 넣는게 더 효율적일 것 같음
        Optional<Point> lastPoint = pointRepository.findLastByUser(user);
        int currentBalance = lastPoint.map(Point::getBalance).orElse(0);

        return Point.charge(user, amount, currentBalance);
    }

    public Optional<Point> getLastPoint(User user) {
        if(user == null) {
            return Optional.empty();
        }

        return pointRepository.findLastByUser(user);
    }

}

package com.loopers.application.point;

import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointService;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserService;
import com.loopers.interfaces.api.point.PointV1RequestDto;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class PointFacade {
    private final PointService pointService;
    private final UserService userService;

    public PointInfo chargePoint(String account, PointV1RequestDto.PointChargeRequest chargeRequest) {
        User user = userService.getUser(account).orElseThrow(() ->
                new CoreException(ErrorType.NOT_FOUND, "[account = " + account + "] 존재하지 않는 회원입니다.")
        );

        Point point = pointService.chargePoint(user, chargeRequest.amount());

        return PointInfo.from(user, point);
    }

    public PointInfo getBalance(String account) {
        User user = userService.getUser(account).orElseThrow(() ->
                new CoreException(ErrorType.NOT_FOUND, "[account = " + account + "] 존재하지 않는 회원입니다.")
        );

        Optional<Point> lastPoint = pointService.getLastPoint(user);
        if (lastPoint.isEmpty()) {
            return new PointInfo(user.getAccount(), 0);
        }

        return PointInfo.from(user, lastPoint.get());
    }
}

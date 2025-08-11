package com.loopers.application.point;

import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointService;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserService;
import com.loopers.interfaces.api.point.PointV1Request;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Transactional
public class PointFacade {
    private final PointService pointService;
    private final UserService userService;

    public PointResult charge(Long userId, PointV1Request.PointChargeRequest chargeRequest) {
        User user = userService.getUser(userId).orElseThrow(() ->
                new CoreException(ErrorType.NOT_FOUND, "[account = " + userId + "] 존재하지 않는 회원입니다.")
        );

        Point point = pointService.charge(chargeRequest.toCommand(user.getId()));

        return PointResult.from(point);
    }

    public PointResult getBalance(Long userId) {
        User user = userService.getUser(userId).orElseThrow(() ->
                new CoreException(ErrorType.NOT_FOUND, "[account = " + userId + "] 존재하지 않는 회원입니다.")
        );

        Point point = pointService.getBy(user.getId()).orElseThrow(() ->
                new CoreException(ErrorType.NOT_FOUND, "[account = " + userId + "] 포인트 정보가 없습니다.")
        );

        return PointResult.from(point);
    }
}

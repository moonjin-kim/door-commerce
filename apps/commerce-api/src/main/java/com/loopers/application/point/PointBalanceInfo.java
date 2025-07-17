package com.loopers.application.point;

import com.loopers.domain.point.Point;
import com.loopers.domain.user.User;

public record PointBalanceInfo(String account, int balance) {
    public static PointBalanceInfo from(User user, Point point) {
        return new PointBalanceInfo(
                user.getAccount(),
                point.getBalance()
        );
    }
}

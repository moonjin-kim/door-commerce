package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointResult;

public class PointV1ResponseDto {
    public record PointBalance(
            Long userId,
            int balance
    ){
        public static PointBalance from(PointResult pointBalanceInfo) {
            return new PointBalance(
                    pointBalanceInfo.userId(),
                    pointBalanceInfo.balance()
            );
        }
    }
}

package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointInfo;

public class PointV1ResponseDto {
    public record PointBalance(
            Long userId,
            int balance
    ){
        public static PointBalance from(PointInfo pointBalanceInfo) {
            return new PointBalance(
                    pointBalanceInfo.userId(),
                    pointBalanceInfo.balance()
            );
        }
    }
}

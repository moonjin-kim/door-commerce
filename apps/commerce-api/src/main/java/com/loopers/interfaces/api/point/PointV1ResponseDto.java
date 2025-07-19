package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointInfo;

public class PointV1ResponseDto {
    public record PointBalanceResponse(
            Long userId,
            int balance
    ){
        public static PointBalanceResponse from(PointInfo pointBalanceInfo) {
            return new PointBalanceResponse(
                    pointBalanceInfo.userId(),
                    pointBalanceInfo.balance()
            );
        }
    }
}

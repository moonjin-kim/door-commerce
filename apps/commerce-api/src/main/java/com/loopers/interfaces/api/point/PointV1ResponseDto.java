package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointInfo;

public class PointV1ResponseDto {
    public record PointBalanceResponse(
            String account,
            int balance
    ){
        public static PointBalanceResponse from(PointInfo pointBalanceInfo) {
            return new PointBalanceResponse(
                    pointBalanceInfo.account(),
                    pointBalanceInfo.balance()
            );
        }
    }
}

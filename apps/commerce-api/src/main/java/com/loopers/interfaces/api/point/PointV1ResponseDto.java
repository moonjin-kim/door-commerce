package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointBalanceInfo;

public class PointV1ResponseDto {
    public record PointBalanceResponse(
            String account,
            int balance
    ){
        public static PointBalanceResponse from(PointBalanceInfo pointBalanceInfo) {
            return new PointBalanceResponse(
                    pointBalanceInfo.account(),
                    pointBalanceInfo.balance()
            );
        }
    }
}

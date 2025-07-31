package com.loopers.interfaces.api.point;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "POINT V1 API", description = "POINT API 입니다.")
public interface PointV1ApiSpec {
    @Operation(
            summary = "포인트 충전",
            description = "현재 유저에 amont만큼의 포인트를 추가한다."
    )
    ApiResponse<PointV1ResponseDto.PointBalance> register(Long userId, PointV1RequestDto.PointChargeRequest chargeRequest);


    @Operation(
            summary = "포인트 잔액 조회",
            description = "현재 유저의 포인트 잔액을 조회한다"
    )
    ApiResponse<PointV1ResponseDto.PointBalance> getBalance(Long userId);
}

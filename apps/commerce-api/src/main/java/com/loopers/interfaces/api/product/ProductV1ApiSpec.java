package com.loopers.interfaces.api.product;

import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.point.PointV1Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Product V1 API", description = "Product API 입니다.")
public interface ProductV1ApiSpec {

    @Operation(
            summary = "포인트 잔액 조회",
            description = "현재 유저의 포인트 잔액을 조회한다"
    )
    ApiResponse<PointV1Response.PointBalance> getBalance(Long userId);
}

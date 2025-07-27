package com.loopers.interfaces.api.brand;

import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.user.UserV1ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Brand V1 API", description = "Brand API 입니다.")
public interface BrandV1ApiSpec
{
    @Operation(
            summary = "회원조회",
            description = "ID로 유저를 조회합니다."
    )
    ApiResponse<UserV1ResponseDto.User> get(Long userId);
}

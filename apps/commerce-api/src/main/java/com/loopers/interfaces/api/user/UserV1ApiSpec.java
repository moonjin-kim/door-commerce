package com.loopers.interfaces.api.user;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "USER V1 API", description = "User API 입니다.")
public interface UserV1ApiSpec {
    @Operation(
            summary = "회원가입",
            description = "ID, 이메일, 생년월일, 성별로 회원가입한다."
    )
    ApiResponse<UserV1ResponseDto.UserResponse> register(UserV1RequestDto.Register requestBody);

    @Operation(
            summary = "회원조회",
            description = "ID로 유저를 조회합니다."
    )
    ApiResponse<UserV1ResponseDto.UserResponse> me(String account);
}

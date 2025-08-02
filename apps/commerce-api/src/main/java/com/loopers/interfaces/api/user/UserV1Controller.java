package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserFacade;
import com.loopers.application.user.UserInfo;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserV1Controller implements UserV1ApiSpec {

    private final UserFacade userFacade;

    @PostMapping("")
    @Override
    public ApiResponse<UserV1Response.User> register(
            @RequestBody UserV1Request.Register body
    ) {
        UserInfo info = userFacade.registerUser(body);
        return ApiResponse.success(UserV1Response.User.from(info));
    }

    @GetMapping("/me")
    @Override
    public ApiResponse<UserV1Response.User> me(
            @RequestHeader("X-USER-ID") Long userId
    ) {
        UserInfo info = userFacade.getMe(userId);
        return ApiResponse.success(UserV1Response.User.from(info));
    }
}

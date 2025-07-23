package com.loopers.application.user;

import com.loopers.domain.user.User;
import com.loopers.domain.user.UserService;
import com.loopers.interfaces.api.user.UserV1RequestDto;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserFacade {
    private final UserService userService;

    public UserInfo registerUser(UserV1RequestDto.Register request) {
        return UserInfo.from(userService.registerMember(request));
    }

    public UserInfo getMe(Long userId) {
        User user = userService.getUser(userId).orElseThrow(() ->
                new CoreException(ErrorType.NOT_FOUND, "[account = " + userId + "] 존재하지 않는 회원입니다.")
        );

        return UserInfo.from(user);
    }
}

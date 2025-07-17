package com.loopers.fixture;

import com.loopers.domain.user.Gender;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserCommand;
import com.loopers.interfaces.api.user.UserV1RequestDto;

public class UserFixture {
    public static UserV1RequestDto.Register createUserRegisterRequest() {
        return new UserV1RequestDto.Register(
                "홍길동","gil123", "gil1234@gmail.com", "2020-01-01", UserV1RequestDto.GenderRequest.MALE
        );
    }

    public static UserCommand.Create createUserCreateCommand() {
        return new UserCommand.Create(
                "홍길동","gil123", "gil1234@gmail.com", "2020-01-01", Gender.MALE
        );
    }

    public static User createMember() {
        return User.register(createUserCreateCommand());
    }
}

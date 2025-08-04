package com.loopers.fixture;

import com.loopers.domain.user.Gender;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserCommand;
import com.loopers.interfaces.api.user.UserV1Request;

public class UserFixture {
    public static UserV1Request.Register createUserRegisterRequest() {
        return new UserV1Request.Register(
                "홍길동","gil123", "gil1234@gmail.com", "2020-01-01", UserV1Request.GenderRequest.MALE
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

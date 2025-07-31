package com.loopers.application.user;

import com.loopers.domain.user.Gender;
import com.loopers.domain.user.User;

import java.time.LocalDate;

public record UserInfo(
        Long id,
        String account,
        String email,
        LocalDate birthday,
        Gender sex
) {
    public static UserInfo from(User user) {
        return new UserInfo(
                user.getId(),
                user.getAccount().value(),
                user.getEmail().value(),
                user.getBirthday(),
                user.getGender()
        );
    }
}

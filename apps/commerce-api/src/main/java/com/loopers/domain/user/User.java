package com.loopers.domain.user;

import com.loopers.domain.BaseEntity;
//import com.loopers.domain.user.Email;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Column(length = 100, unique = true, nullable = false)
    String account;
    @Column(length = 100, unique = true, nullable = false)
    String name;
    @Column(length = 100, unique = true, nullable = false)
    String email;
    @Column
    LocalDate birthday;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Gender gender;

    public static User register(UserCommand.Create command) {
        User user = new User();

        UserValidator.validateName(command.name());
        user.name = command.name();

        UserValidator.validateAccount(command.account());
        user.account = command.account();

        UserValidator.validateEmail(command.email());
        user.email = command.email();

        UserValidator.validateBirthday(command.birthday());
        user.birthday = LocalDate.parse(command.birthday());

        UserValidator.validateGender(command.gender());
        user.gender = command.gender();

        return user;
    }


}

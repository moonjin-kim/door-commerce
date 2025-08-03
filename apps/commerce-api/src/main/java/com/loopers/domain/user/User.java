package com.loopers.domain.user;

import com.loopers.domain.BaseEntity;
//import com.loopers.domain.user.Email;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

import java.time.LocalDate;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Embedded
    Account account;
    @Column(length = 100, unique = true, nullable = false)
    String name;
    @Column(length = 100, unique = true, nullable = false)
    @Embedded
    @NaturalId
    Email email;
    @Column
    LocalDate birthday;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Gender gender;

    protected User(Account account, String name, Email email, LocalDate birthday, Gender gender) {
        this.account = account;
        this.name = name;
        this.email = email;
        this.birthday = birthday;
        this.gender = gender;
    }

    public static User create(UserCommand.Create command) {
        UserValidator.validateName(command.name());
        UserValidator.validateBirthday(command.birthday());
        UserValidator.validateGender(command.gender());

        User user = new User();

        user.name = command.name();
        user.account = new Account(command.account());
        user.email = new Email(command.email());
        user.birthday = LocalDate.parse(command.birthday());
        user.gender = command.gender();

        return user;
    }


}

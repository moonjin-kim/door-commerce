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

    protected User(String account, String name, String email, String birthday, Gender gender){
        UserValidator.validateName(name);
        UserValidator.validateBirthday(birthday);
        UserValidator.validateGender(gender);

        this.account = new Account(account);
        this.name = name;
        this.email = new Email(email);
        this.birthday = LocalDate.parse(birthday);;
        this.gender = gender;
    }

    public static User create(UserCommand.Create command) {

        return new User(
                command.account(),
                command.name(),
                command.email(),
                command.birthday(),
                command.gender()
        );
    }


}

package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import java.util.regex.Pattern;

public class UserValidator {
    private static final String ACCOUNT_REGEX = "^[a-zA-Z0-9]{1,10}$";
    private static final String BIRTH_DATE_REGEX =
            "^(19|20)\\d{2}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$";
    static String EMAIL_PATTERN = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9_+&*-]+\\.)+[a-zA-Z]{2,7}$";

    static void validateName(String name) {
        if(name == null || name.isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "아이디는 비어있을 수 없습니다.");
        }
    }

    static void validateEmail(String email) {
        if(email == null || email.isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이메일은 비어있을 수 없습니다.");
        }
        if(!Pattern.matches(EMAIL_PATTERN, email)) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이메일 형식이 잘못되었습니다.");
        }
    }

    static void validateAccount(String account) {
        if(account == null || account.isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "아이디는 비어있을 수 없습니다.");
        }
        if(!Pattern.matches(ACCOUNT_REGEX, account)) {
            throw new CoreException(ErrorType.BAD_REQUEST, "아이디 형식이 잘못되었습니다.");
        }
    }


    static void validateBirthday(String birthday) {
        if(birthday == null || birthday.isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "생년월일은 비어있을 수 없습니다.");
        }

        if(!Pattern.matches(BIRTH_DATE_REGEX, birthday)) {
            throw new CoreException(ErrorType.BAD_REQUEST, "생년월일 형식이 잘못되었습니다.");
        }
    }

    static void validateGender(Gender gender) {
        if(gender == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "성별은 비워있을 수 없습니다.");
        }
    }
}

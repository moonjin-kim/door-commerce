package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Embeddable
@NoArgsConstructor
public class Account {
    @Column(length = 30, nullable = false)
    private String account;

    private static final String ACCOUNT_REGEX = "^[a-zA-Z0-9]{1,10}$";

    public Account(String account) {
        if(account == null || account.isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "아이디는 비어있을 수 없습니다.");
        }
        if(!Pattern.matches(ACCOUNT_REGEX, account)) {
            throw new CoreException(ErrorType.BAD_REQUEST, "아이디 형식이 잘못되었습니다.");
        }
        this.account = account;
    }

    public String value() {
        return account;
    }
}

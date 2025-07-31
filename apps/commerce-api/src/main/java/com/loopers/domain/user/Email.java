package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Embeddable
@NoArgsConstructor
public class Email {
    @Column(name = "email", length = 150, nullable = false)
    private String address;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9_+&*-]+\\.)+[a-zA-Z]{2,7}$");

    public Email(String address) {
        if(address == null || address.isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이메일은 비어있을 수 없습니다.");
        }
        if (!EMAIL_PATTERN.matcher(address).matches()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이메일 형식이 바르지 않습니다: " + address);
        }
        this.address = address;
    }

    public String value() {
        return address;
    }
}

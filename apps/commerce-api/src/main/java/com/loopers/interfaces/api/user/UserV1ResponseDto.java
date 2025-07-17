package com.loopers.interfaces.api.user;

import com.loopers.domain.user.Gender;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import java.time.LocalDate;

public class UserV1ResponseDto {
    public record UserResponse(
            Long id,
            String account,
            String email,
            LocalDate birthday,
            GenderResponse gender
    ){
    }

    public enum GenderResponse {
        MALE,
        FEMALE;

        // 변환 로직을 Enum 내부에 구현
        public static GenderResponse from(Gender gender) {
            switch (gender) {
                case Gender.MALE:
                    return MALE;
                case Gender.FEMALE:
                    return FEMALE;
                default:
                    throw new CoreException(ErrorType.BAD_REQUEST, "생년월일은 비어있을 수 없습니다.");
            }
        }
    }
}

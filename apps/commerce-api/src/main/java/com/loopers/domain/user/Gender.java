package com.loopers.domain.user;

import com.loopers.interfaces.api.user.UserV1RequestDto;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

public enum Gender {
    MALE,
    FEMALE;

    public static Gender from(UserV1RequestDto.GenderRequest gender) {
        if (gender == null) {
            return null;
        }
        switch (gender) {
            case UserV1RequestDto.GenderRequest.MALE:
                return Gender.MALE;
            case UserV1RequestDto.GenderRequest.FEMALE:
                return Gender.FEMALE;
            default:
                throw new CoreException(ErrorType.BAD_REQUEST, "생년월일은 비어있을 수 없습니다.");
        }
    }
}

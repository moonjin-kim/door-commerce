package com.loopers.domain.user;

import com.loopers.interfaces.api.user.UserV1Request;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

public enum Gender {
    MALE,
    FEMALE;

    public static Gender from(UserV1Request.GenderRequest gender) {
        if (gender == null) {
            return null;
        }
        switch (gender) {
            case UserV1Request.GenderRequest.MALE:
                return Gender.MALE;
            case UserV1Request.GenderRequest.FEMALE:
                return Gender.FEMALE;
            default:
                throw new CoreException(ErrorType.BAD_REQUEST, "생년월일은 비어있을 수 없습니다.");
        }
    }
}

package com.loopers.domain.brand;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

public class BrandValidator {
    static void validateName(String name) {
        if(name == null || name.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "브랜드 이름은 비어있을 수 없습니다.");
        }

        if(name.length() > 20) {
            throw new CoreException(ErrorType.BAD_REQUEST, "브랜드 이름은 20자 이하여야 합니다.");
        }
    }

    static void validateDescription(String description) {
        if(description == null || description.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "브랜드 정보은 비어있을 수 없습니다.");
        }
    }

    static void validateLogoUrl(String logoUrl) {
        if(logoUrl == null || logoUrl.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "로고 이미지는 비어있을 수 없습니다..");
        }

        if(!logoUrl.startsWith("http://") && !logoUrl.startsWith("https://")) {
            throw new CoreException(ErrorType.BAD_REQUEST, "로고 이미지 url 형식이 잘못되었습니다.");
        }
    }
}

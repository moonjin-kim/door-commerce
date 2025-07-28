package com.loopers.domain.product;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

public class ProductValidator {
    static void validateBrandId(Long brandId) {
        if(brandId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "브랜드 id는 비어있을 수 없습니다.");
        }
    }

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

    static void validateImageUrl(String imageUrl) {
        if(imageUrl == null || imageUrl.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "로고 이미지는 비어있을 수 없습니다..");
        }

        if(!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
            throw new CoreException(ErrorType.BAD_REQUEST, "로고 이미지 url 형식이 잘못되었습니다.");
        }
    }
}

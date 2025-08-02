package com.loopers.domain.brand;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BrandTest {

    @DisplayName("브랜드를 생성할 때,")
    @Nested
    class Create{
        @DisplayName("이름 형식이 잘못되면 BadRequest 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {
                "",
                "가나다라마바사아자차카타파하아야어이브랜드"
        })
        void throwBadRequest_UnValidNameInfo(String name){
            //given
            BrandCommand.Create command = BrandCommand.Create.of(
                    name,
                    "루퍼스는 루퍼스입니다.",
                    "https://loopers.com/logo.png"
            );

            //when
            CoreException result = assertThrows(CoreException.class, () -> {
                Brand.create(command);
            });

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("설명이 주어지지 않으면 BadRequest 예외가 발생한다.")
        @Test
        void throwBadRequest_InValidDescription(){
            //given
            BrandCommand.Create command = BrandCommand.Create.of(
                    "루퍼스",
                    "",
                    "https://loopers.com/logo.png"
            );

            //when
            CoreException result = assertThrows(CoreException.class, () -> {
                Brand.create(command);
            });

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("로그 이미지 주소 형식이 잘못되었으면 BadRequest 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {
                "",
                "loopers.com/logo.png",
        })
        void throwBadRequest_InValidLogoUrl(String logoUrl){
            //given
            BrandCommand.Create command = BrandCommand.Create.of(
                    "루퍼스",
                    "루퍼스는 루퍼스입니다.",
                    logoUrl
            );

            //when
            CoreException result = assertThrows(CoreException.class, () -> {
                Brand.create(command);
            });

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("브랜드 정보가 주어지면, Brand 객체를 반환한다.")
        @Test
        void returnBrand_whenBrandInfo(){
            //given
            BrandCommand.Create command = BrandCommand.Create.of(
                    "루퍼스",
                    "루퍼스는 루퍼스입니다.",
                    "https://loopers.com/logo.png"
            );

            //when
            Brand brand = Brand.create(command);

            //then
            assertAll(
                    () -> assertThat(brand.getName()).isEqualTo("루퍼스"),
                    () -> assertThat(brand.getDescription()).isEqualTo("루퍼스는 루퍼스입니다."),
                    () -> assertThat(brand.getLogoUrl()).isEqualTo("https://loopers.com/logo.png")
            );
        }
    }
}

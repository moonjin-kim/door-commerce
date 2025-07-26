package com.loopers.domain.user;

import com.loopers.fixture.UserFixture;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {
    @DisplayName("회원가입할 때")
    @Nested
    class Register {
        @DisplayName("회원 정보가 모두 주어지면 회원가입에 성공한다.")
        @Test
        void registerMember(){
            //given
            UserCommand.Create request = UserFixture.createUserCreateCommand();

            //when
            User user = User.register(request);

            //then
            assertThat(user.getId()).isNotNull();
            assertThat(user.getName()).isEqualTo(request.name());
            assertThat(user.getAccount().value()).isEqualTo(request.account());
            assertThat(user.getEmail().value()).isEqualTo(request.email());
            assertThat(user.getBirthday()).isEqualTo(request.birthday());
            assertThat(user.getGender()).isEqualTo(request.gender());

        }

        @DisplayName("ID 가 영문 및 숫자 10자 이내 형식에 맞지 않으면, User 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenNameIsEmpty(){
            //given
            UserCommand.Create request1 = new UserCommand.Create(
                    "홍길동", null,"gildong@gmail.com", "2020-01-01", Gender.MALE
            );

            //when
            CoreException result = assertThrows(CoreException.class, () -> {
                User.register(request1);
            });

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("ID 가 영문 및 숫자 10자 이내 형식에 맞지 않으면, User 객체 생성에 실패한다.")
        @ParameterizedTest
        @ValueSource(strings = {
                "gildong1234",
                "홍길동1234",
                "홍길동@1234",
                ""
        })
        void throwsBadRequestException_whenAccountLenOverTen(String account){
            //given
            UserCommand.Create request1 = new UserCommand.Create(
                    "홍길동", account,"gildong@gmail.com", "2020-01-01", Gender.MALE
            );

            //when
            CoreException result = assertThrows(CoreException.class, () -> {
                User.register(request1);
            });

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("이메일이 xx@yy.zz 형식에 맞지 않으면, User 객체 생성에 실패한다.")
        @ParameterizedTest
        @ValueSource(strings = {
                "gildong@gmail",
                "gildong",
                "@naver.com",
                ""
        })
        void throwsBadRequestException_whenIncorrectEmailFormat(String email){
            //given
            UserCommand.Create request = new UserCommand.Create(
                    "홍길동","gil123",email, "2020-01-01", Gender.MALE
            );

            //when
            CoreException result = assertThrows(CoreException.class, () -> {
                User.register(request);
            });

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("생년월일이 yyyy-MM-dd 형식에 맞지 않으면, User 객체 생성에 실패한다.")
        @ParameterizedTest
        @ValueSource(strings = {
                "20250101",
                "2025-01-",
                "2020-01-0",
                "",
        })
        void throwsBadRequestException_whenIncorrectBirthDayFormat(String birthday){
            //given
            UserCommand.Create request = new UserCommand.Create(
                    "홍길동","gildong","gildong@gmail.com", birthday, Gender.MALE
            );

            //when
            CoreException result = assertThrows(CoreException.class, () -> {
                User.register(request);
            });

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("성별을 받지 못하면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsBadRequestException_whenIncorrectSex(){
            //given
            UserCommand.Create request = new UserCommand.Create(
                    "홍길동","gildong","gildong@gmail.com", "2020-01-01", null
            );

            //when
            CoreException result = assertThrows(CoreException.class, () -> {
                User.register(request);
            });

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }
}

package com.loopers.domain.user;

import com.loopers.fixture.UserFixture;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.interfaces.api.user.UserV1RequestDto;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class UserServiceIntegrationTest {
    @MockitoSpyBean
    private UserJpaRepository userJpaRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("회원가입 시도 할 때,")
    @Nested
    class register {
        @DisplayName("모든 정보가 주어졌을때, 유저정보가 DB에 저장된다.")
        @Test
        void registerMember_whenAllMemberInfoAreProvide(){
            //given
            UserV1RequestDto.Register request = UserFixture.createUserRegisterRequest();

            //when
            User user = userService.registerMember(request);

            //then
            verify(userJpaRepository, times(1)).save(any(User.class));
            assertThat(user.getId()).isNotNull();
            User savedUser = userJpaRepository.findAll().getFirst();
            assertAll(
                    () -> assertThat(savedUser.getId()).isEqualTo(user.getId()),
                    () -> assertThat(savedUser.getName()).isEqualTo(user.getName()),
                    () -> assertThat(savedUser.getAccount()).isEqualTo(request.account()),
                    () -> assertThat(savedUser.getEmail()).isEqualTo(request.email()),
                    () -> assertThat(savedUser.getBirthday()).isEqualTo(request.birthday()),
                    () -> assertThat(savedUser.getGender()).isEqualTo(user.gender)
            );
        }

        @DisplayName("이미 해당 아이디로 회원가입된 멤버가 존재시, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsException_whenAlreadyRegisteredMember() {
            //given
            UserV1RequestDto.Register request = UserFixture.createUserRegisterRequest();
            User user = userJpaRepository.save(
                    User.register(request.toCommand())
            );

            //when
            CoreException exception = assertThrows(CoreException.class, () -> {
                userService.registerMember(request);
            });

            //then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayName("회원정보를 조회할 때")
    @Nested
    class getUser {
        @DisplayName("해당 ID 의 회원이 존재할 경우, 회원 정보가 반환된다.")
        @Test
        void returnsUser_whenValidIdIsProvided(){
            //given
            User user = userJpaRepository.save(
                    UserFixture.createMember()
            );

            //when
            User result = userService.getUser(user.getId()).orElse(null);

            //then

            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getId()).isEqualTo(user.getId()),
                    () -> assertThat(result.getName()).isEqualTo(user.getName()),
                    () -> assertThat(result.getAccount()).isEqualTo(user.getAccount()),
                    () -> assertThat(result.getEmail()).isEqualTo(user.getEmail()),
                    () -> assertThat(result.getBirthday()).isEqualTo(user.getBirthday()),
                    () -> assertThat(result.getGender()).isEqualTo(user.getGender())
            );
        }

        @DisplayName("존재하지 않는 유저 ID를 주면, null을 반환한다.")
        @Test
        void throwsException_whenInvalidIdIsProvided(){
            //given

            //when
            Optional<User> result = userService.getUser(1L);

            // assert
            assertThat(result.isPresent()).isFalse();
        }
    }
}

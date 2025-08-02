package com.loopers.application.point;

import com.loopers.domain.point.Point;
import com.loopers.domain.user.User;
import com.loopers.fixture.UserFixture;
import com.loopers.infrastructure.point.PointJpaRepository;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.interfaces.api.point.PointV1Request;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class PointFacadeTest {
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private PointJpaRepository pointJpaRepository;
    @Autowired
    private PointFacade pointFacade;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("유저가 포인트 충전 시,")
    @Nested
    class chargePoint {

        @DisplayName("유저에 포인트가 충전된다.")
        @Test
        void returnPoint(){
            //given
            User user = userJpaRepository.save(UserFixture.createMember());
            var chargeRequest = new PointV1Request.PointChargeRequest(1000L);

            //when
            PointResult result = pointFacade.charge(user.getId(), chargeRequest);

            //then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.userId()).isEqualTo(user.getId()),
                    () -> assertThat(result.balance()).isEqualTo(chargeRequest.amount())
            );
        }

        @DisplayName("유저의 잔액이 남아있으면 잔액에 충전 포인트가 더해진다.")
        @Test
        void addPoint_whenRemainBalance(){
            //given
            User user = userJpaRepository.save(UserFixture.createMember());

            Point point = Point.init(user.getId());
            point.charge(1000);
            Point chargedPoint = pointJpaRepository.save(
                    point
            );

            var chargeRequest = new PointV1Request.PointChargeRequest(1000L);

            //when
            PointResult result = pointFacade.charge(user.getId(), chargeRequest);

            //then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.userId()).isEqualTo(user.getId()),
                    () -> assertThat(result.balance()).isEqualTo(
                            chargedPoint.getBalance().plus(chargeRequest.amount()).value()
                    )
            );
        }

        @DisplayName("존재하지 않는 유저 충전을 시도한 경우, 실패한다.")
        @Test
        void throwException_whenInvalidIdIsProvided(){
            //given
            var chargeRequest = new PointV1Request.PointChargeRequest(1000L);

            //when
            CoreException exception = assertThrows(CoreException.class, () -> {
                pointFacade.charge(100L, chargeRequest);
            });

            //then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }

    @DisplayName("포인트 잔액을 조회할 때")
    @Nested
    class getBalance {

        @DisplayName("유저가 존재할 경우, 보유 포인트가 반환된다.")
        @Test
        void returnPoint_whenValidIdIsProvided(){
            //given
            User user = userJpaRepository.save(
                    UserFixture.createMember()
            );

            Point point = Point.init(user.getId());
            point.charge(1000);
            Point chargedPoint = pointJpaRepository.save(
                   point
            );

            //when
            PointResult result = pointFacade.getBalance(user.getId());

            //then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.userId()).isEqualTo(user.getId()),
                    () -> assertThat(result.balance()).isEqualTo(chargedPoint.getBalance().value())
            );
        }

        @DisplayName("유저가 존재하지 않을 경우, NOT_FOUND 예외가 발생한다.")
        @Test
        void throwException_whenInValidIdIsProvided(){
            //given

            //when
            CoreException exception = assertThrows(CoreException.class, () -> {
                pointFacade.getBalance(100L);
            });

            //then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }

        @DisplayName("유저의 포인트가 없는 경우, NOT_FOUND 예외가 발생한다.")
        @Test
        void savePoint_whenPointDoesntExist(){
            //given
            User user = userJpaRepository.save(
                    UserFixture.createMember()
            );

            //when
            CoreException exception = assertThrows(CoreException.class, () -> {
                pointFacade.getBalance(user.getId());
            });

            //then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }
}

package com.loopers.application.point;

import com.loopers.domain.point.Point;
import com.loopers.domain.user.User;
import com.loopers.fixture.UserFixture;
import com.loopers.infrastructure.point.PointJpaRepository;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.interfaces.api.point.PointV1RequestDto;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

        @DisplayName("해당 ID에 포인트가 충전된다.")
        @Test
        void returnPoint(){
            //given
            User user = userJpaRepository.save(UserFixture.createMember());
            var chargeRequest = new PointV1RequestDto.PointChargeRequest(1000);

            //when
            PointInfo result = pointFacade.charge(user.getAccount(), chargeRequest);

            //then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.account()).isEqualTo(user.getAccount()),
                    () -> assertThat(result.balance()).isEqualTo(chargeRequest.amount())
            );
        }

        @DisplayName("해당 ID에 잔액이 남아있으면 잔액에 충전 포인트가 더해진다.")
        @Test
        void addPoint_whenRemainBalance(){
            //given
            User user = userJpaRepository.save(UserFixture.createMember());
            Point chargedPoint = pointJpaRepository.save(
                    Point.charge(user, 1000, 0)
            );
            var chargeRequest = new PointV1RequestDto.PointChargeRequest(1000);

            //when
            PointInfo result = pointFacade.charge(user.getAccount(), chargeRequest);

            //then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.account()).isEqualTo(user.getAccount()),
                    () -> assertThat(result.balance()).isEqualTo(chargedPoint.getBalance() + chargeRequest.amount())
            );
        }

        @DisplayName("존재하지 않는 유저 ID 로 충전을 시도한 경우, 실패한다.")
        @Test
        void throwException_whenInvalidIdIsProvided(){
            //given
            var chargeRequest = new PointV1RequestDto.PointChargeRequest(1000);

            //when
            CoreException exception = assertThrows(CoreException.class, () -> {
                pointFacade.charge("human", chargeRequest);
            });

            //then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }

    @DisplayName("포인트 잔액을 조회할 때")
    @Nested
    class getBalance {

        @DisplayName("해당 ID 의 회원이 존재할 경우, 보유 포인트가 반환된다.")
        @Test
        void returnPoint_whenValidIdIsProvided(){
            //given
            User user = userJpaRepository.save(
                    UserFixture.createMember()
            );
            Point chargedPoint = pointJpaRepository.save(
                    Point.charge(user, 10000, 0)
            );

            //when
            PointInfo result = pointFacade.getBalance(user.getAccount());

            //then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.account()).isEqualTo(user.getAccount()),
                    () -> assertThat(result.balance()).isEqualTo(chargedPoint.getBalance())
            );
        }

        @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, NOT_FOUND 예외가 발생한다.")
        @Test
        void throwException_whenInValidIdIsProvided(){
            //given

            //when
            CoreException exception = assertThrows(CoreException.class, () -> {
                pointFacade.getBalance("human");
            });

            //then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }
}

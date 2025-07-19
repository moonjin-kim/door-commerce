package com.loopers.domain.point;

import com.loopers.domain.user.User;
import com.loopers.fixture.UserFixture;
import com.loopers.infrastructure.point.PointJpaRepository;
import com.loopers.infrastructure.user.UserJpaRepository;
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

@SpringBootTest
public class PointServiceIntegrationTest {
    @MockitoSpyBean
    private UserJpaRepository userJpaRepository;
    @Autowired
    private PointJpaRepository pointJpaRepository;
    @Autowired
    private PointService pointService;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("포인트가 새로 생성할 때")
    @Nested
    class initPoint {
        @DisplayName("DB에 User ID에 해당하는 포인트 존재하면 BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwBadRequest_whenAlreadySavePointByUserId(){
            //given
            Long userId = 1L;
            pointJpaRepository.save(Point.init(userId));

            //when
            CoreException exception = assertThrows(CoreException.class, () -> {
                pointService.initPoint(userId);
            });

            //then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("DB에 User ID에 해당하는 포인트가 저장된다.")
        @Test
        void savePoint(){
            //given
            Long userId = 1L;

            //when
            Point point = pointService.initPoint(userId);

            //then
            Optional<Point> savedPoint = pointJpaRepository.findByUserId(userId);
            assertAll(
                    () -> assertThat(savedPoint).isPresent(),
                    () -> assertThat(savedPoint.get().getId()).isEqualTo(point.getId()),
                    () -> assertThat(savedPoint.get().getUserId()).isEqualTo(userId),
                    () -> assertThat(savedPoint.get().getBalance()).isEqualTo(0)
            );
        }
    }

    @DisplayName("포인트를 충전할 때")
    @Nested
    class ChargePoint {
        @DisplayName("해당 ID에 포인트가 충전된다.")
        @Test
        void returnPoint(){
            //given
            User user = userJpaRepository.save(UserFixture.createMember());
            int amount = 10000;

            //when
            Point result = pointService.chargePoint(user.getId(), amount);

            //then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getUserId()).isEqualTo(user.getId()),
                    () -> assertThat(result.getBalance()).isEqualTo(amount)
            );
        }

        @DisplayName("해당 ID에 잔액이 남아있으면 잔액에 충전 포인트가 더해진다.")
        @Test
        void addPoint_whenRemainBalance(){
            //given
            User user = userJpaRepository.save(UserFixture.createMember());

            Point point = Point.init(user.getId());
            point.charge(1000);
            Point chargedPoint = pointJpaRepository.save(
                    point
            );
            int amount = 10000;

            //when
            Point result = pointService.chargePoint(user.getId(), amount);

            //then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getUserId()).isEqualTo(user.getId()),
                    () -> assertThat(result.getBalance()).isEqualTo(point.balance + amount)
            );
        }

        @DisplayName("유저가 null이면, NOT_FOUND 예외가 발생한다.")
        @Test
        void throwNotFound_whenNullUser(){
            //given
            User user = userJpaRepository.save(UserFixture.createMember());
            Point point = Point.init(user.getId());
            point.charge(1000);
            Point chargedPoint = pointJpaRepository.save(
                    point
            );
            int amount = 10000;

            //when
            CoreException exception = assertThrows(CoreException.class, () -> {
                pointService.chargePoint(null, amount);
            });

            //then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }

        @DisplayName("0원 이하의 포인트를 충전 요청하면, INVALID_POINT_AMOUNT 예외가 발생한다.")
        @Test
        void throwInvalidPointAmount_whenZeroPoint(){
            //given
            User user = userJpaRepository.save(UserFixture.createMember());
            int amount = 0;

            //when
            CoreException exception = assertThrows(CoreException.class, () -> {
                pointService.chargePoint(user.getId(), amount);
            });

            //then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.INVALID_POINT_AMOUNT);
        }
    }


    @DisplayName("포인트 조회시")
    @Nested
    class getBalance {

        @DisplayName("보유 포인트가 존재하면, 보유 포인트가 반환된다.")
        @Test
        void returnLastPoint_whenValidIdIsProvided(){
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
            Point result = pointService.getPoint(user.getId()).orElse(null);

            //then
            assertThat(result.balance).isEqualTo(point.balance);
        }

//        @DisplayName("보유 포인트가 존재하지 않으면, null이 반환된다.")
//        @Test
//        void returnNullPoint_whenValidIdIsProvidedAndNoChargeHistory(){
//            //given
//            User user = userJpaRepository.save(
//                    UserFixture.createMember()
//            );
//
//            //when
//            Optional<Point> result = pointService.getLastPoint(user);
//
//            //then
//            assertThat(result.isPresent()).isFalse();
//        }

        @DisplayName("존재하지 않는 유저이면, null이 반환된다.")
        @Test
        void returnNullPoint_whenUserNotExist(){
            //given

            //when
            Optional<Point> result = pointService.getPoint(null);

            //then
            assertThat(result.isPresent()).isFalse();
        }
    }
}

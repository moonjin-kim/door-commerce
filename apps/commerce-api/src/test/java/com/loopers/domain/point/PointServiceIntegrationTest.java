package com.loopers.domain.point;

import com.loopers.domain.user.User;
import com.loopers.fixture.UserFixture;
import com.loopers.infrastructure.point.PointHistoryJpaRepository;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class PointServiceIntegrationTest {
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private PointJpaRepository pointJpaRepository;
    @Autowired
    private PointHistoryJpaRepository pointHistoryJpaRepository;
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
            pointJpaRepository.save(Point.create(userId));

            //when
            CoreException exception = assertThrows(CoreException.class, () -> {
                pointService.init(userId);
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
            Point point = pointService.init(userId);

            //then
            Optional<Point> savedPoint = pointJpaRepository.findByUserId(userId);
            assertAll(
                    () -> assertThat(savedPoint).isPresent(),
                    () -> assertThat(savedPoint.get().getId()).isEqualTo(point.getId()),
                    () -> assertThat(savedPoint.get().getUserId()).isEqualTo(userId),
                    () -> assertThat(savedPoint.get().balance().longValue()).isEqualTo(0)
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
            PointCommand.Charge chargeCommand = PointCommand.Charge.of(user.getId(), 10000L);

            //when
            Point result = pointService.charge(chargeCommand);

            //then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getUserId()).isEqualTo(user.getId()),
                    () -> assertThat(result.getBalance().longValue()).isEqualTo(chargeCommand.amount())
            );
        }

        @DisplayName("포인트가 정상적으로 충전되면 포인트 충전 이력이 저장된다.")
        @Test
        void savedRequest_whenAmountLessThanBalance() {
            //given
            User user = userJpaRepository.save(UserFixture.createMember());
            Point point = pointJpaRepository.save(Point.create(user.getId()));

            //when
            Point result = pointService.charge(PointCommand.Charge.of(user.getId(),500L));

            //then
            List<PointHistory> savedPoint = pointHistoryJpaRepository.findAll();
            assertThat(savedPoint).hasSize(1);
            assertThat(savedPoint.get(0).getStatus()).isEqualTo(PointStatus.CHARGE);
            assertThat(savedPoint.get(0).getPointId()).isEqualTo(point.getId());
            assertThat(savedPoint.get(0).getAmount().longValue()).isEqualTo(500L);
            assertThat(result.getBalance().longValue()).isEqualTo(500L);
        }

        @DisplayName("해당 ID에 잔액이 남아있으면 잔액에 충전 포인트가 더해진다.")
        @Test
        void addPoint_whenRemainBalance(){
            //given
            User user = userJpaRepository.save(UserFixture.createMember());

            Point point = Point.create(user.getId());
            point.charge(1000);
            Point chargedPoint = pointJpaRepository.save(
                    point
            );
            PointCommand.Charge chargeCommand = PointCommand.Charge.of(user.getId(), 10000L);

            //when
            Point result = pointService.charge(chargeCommand);

            //then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getUserId()).isEqualTo(user.getId()),
                    () -> assertThat(result.getBalance().longValue()).isEqualTo(point.balance().plus(
                            BigDecimal.valueOf(chargeCommand.amount())
                    ).longValue())
            );
        }

        @DisplayName("유저가 null이면, NOT_FOUND 예외가 발생한다.")
        @Test
        void throwNotFound_whenNullUser(){
            //given
            User user = userJpaRepository.save(UserFixture.createMember());
            Point point = Point.create(user.getId());
            point.charge(1000);
            Point chargedPoint = pointJpaRepository.save(
                    point
            );
            PointCommand.Charge chargeCommand = PointCommand.Charge.of(null, 10000L);

            //when
            CoreException exception = assertThrows(CoreException.class, () -> {
                pointService.charge(chargeCommand);
            });

            //then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }

        @DisplayName("0원 이하의 포인트를 충전 요청하면, INVALID_POINT_AMOUNT 예외가 발생한다.")
        @Test
        void throwInvalidPointAmount_whenZeroPoint(){
            //given
            User user = userJpaRepository.save(UserFixture.createMember());
            PointCommand.Charge chargeCommand = PointCommand.Charge.of(user.getId(), 0L);

            //when
            CoreException exception = assertThrows(CoreException.class, () -> {
                pointService.charge(chargeCommand);
            });

            //then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.INVALID_POINT_AMOUNT);
        }


        @DisplayName("포인트가 존재하지 않을때, 포인트가 생성된 후 포인트를 충전한다.")
        @Test
        void saveNewPointAndCharge_whenPointExist(){
            //given
            User user = userJpaRepository.save(UserFixture.createMember());
            Long amount = 1000L;
            PointCommand.Charge chargeCommand = PointCommand.Charge.of(user.getId(), amount);

            //when
            Point result = pointService.charge(chargeCommand);

            //then
            Optional<Point> savedPoint = pointJpaRepository.findByUserId(user.getId());
            assertAll(
                    () -> assertThat(savedPoint).isPresent(),
                    () -> assertThat(savedPoint.get().getUserId()).isEqualTo(user.getId()),
                    () -> assertThat(savedPoint.get().balance().longValue()).isEqualTo(amount)
            );
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
            Point point = Point.create(user.getId());
            point.charge(1000);
            Point chargedPoint = pointJpaRepository.save(
                    point
            );

            //when
            Optional<Point> result = pointService.getBy(user.getId());

            //then

            assertThat(result.get().balance().longValue()).isEqualTo(point.balance().longValue());
        }

        @DisplayName("존재하지 않는 유저이면, NotFound 예외가 발생한다.")
        @Test
        void throwNotFound_whenUserNotExist(){
            //given

            //when
            Optional<Point> result = pointService.getBy(2L);

            //then
            assertThat(result.isPresent()).isFalse();
        }
    }

    @DisplayName("포인트를 사용할 때,")
    @Nested
    class Using {
        @DisplayName("잔액보다 많은 금액 사용요청이 발생하면, INVALID_INPUT 예외가 발생한다.")
        @Test
        void throwInsufficientBalance_whenAmountMoreThanBalance() {
            //given
            User user = userJpaRepository.save(UserFixture.createMember());
            Point point = Point.create(user.getId());
            point.charge(1000);
            pointJpaRepository.save(point);

            //when
            CoreException exception = assertThrows(CoreException.class, () -> {
                pointService.using(PointCommand.Using.of(user.getId(), "123456",2000L));
            });

            //then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }

        @DisplayName("포인트 정보가 없으면, NOT_FOUND 예외가 발생한다.")
        @Test
        void throwNotFound_whenNotFoundPoint() {
            //given
            User user = userJpaRepository.save(UserFixture.createMember());

            //when
            CoreException exception = assertThrows(CoreException.class, () -> {
                pointService.using(PointCommand.Using.of(user.getId(), "123456",2000L));
            });

            //then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }

        @DisplayName("잔액보다 적은 금액 사용요청이 발생하면, 잔액에서 차감된다.")
        @Test
        void deductBalance_whenAmountLessThanBalance() {
            //given
            User user = userJpaRepository.save(UserFixture.createMember());
            Point point = Point.create(user.getId());
            point.charge(1000);
            pointJpaRepository.save(point);

            //when
            Point result = pointService.using(PointCommand.Using.of(user.getId(), "123456",500L));

            //then
            assertThat(result.balance().longValue()).isEqualTo(500L);
        }

        @DisplayName("잔액이 정상적으로 차감되면 잔액 차감 후 포인트 사용 이력이 저장된다.")
        @Test
        void savedRequest_whenAmountLessThanBalance() {
            //given
            User user = userJpaRepository.save(UserFixture.createMember());
            Point point = Point.create(user.getId());
            point.charge(1000);
            pointJpaRepository.save(point);

            //when
            Point result = pointService.using(PointCommand.Using.of(user.getId(), "123456",500L));

            //then
            Optional<PointHistory> savedPoint = pointHistoryJpaRepository.findByOrderId("123456");
            assertThat(savedPoint).isPresent();
            assertThat(savedPoint.get().getStatus()).isEqualTo(PointStatus.USE);
            assertThat(savedPoint.get().getAmount().longValue()).isEqualTo(-500L);
            assertThat(result.balance().longValue()).isEqualTo(500L);
        }

        @DisplayName("0 이하의 금액을 사용 요청하면, INVALID_POINT_AMOUNT 예외가 발생한다.")
        @Test
        void throwInvalidPointAmount_whenZeroOrLessAmount() {
            //given
            User user = userJpaRepository.save(UserFixture.createMember());
            Point point = Point.create(user.getId());
            point.charge(1000);
            pointJpaRepository.save(point);

            //when
            CoreException exception = assertThrows(CoreException.class, () -> {
                pointService.using(PointCommand.Using.of(user.getId(), "123456",0L));
            });

            //then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.INVALID_POINT_AMOUNT);
        }
    }
}

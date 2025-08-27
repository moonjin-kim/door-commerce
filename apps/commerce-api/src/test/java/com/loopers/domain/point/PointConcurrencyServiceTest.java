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
public class PointConcurrencyServiceTest {
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

    @DisplayName("포인트를 충전할 때")
    @Nested
    class ChargePoint {
        @DisplayName("포인트가 동시에 충전요청을 하여도 정상적으로 포인트가 충전된다.")
        @Test
        void chargeSuccess_whenPointChargeSimultaneously() throws InterruptedException  {
            //given
            int threadCount = 10;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            User user = userJpaRepository.save(UserFixture.createMember());
            Point point = Point.create(user.getId());
            point = pointJpaRepository.save(point);

            // 포인트 사용 실패 에러 저장 위치
            List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());

            //when
            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        pointService.charge(PointCommand.Charge.of(user.getId(), 10000L));
                    } catch (Exception e) {
                        exceptions.add(e);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            //then
            latch.await();
            Point afterPoint = pointJpaRepository.findById(point.getId()).orElseThrow();
            assertThat(afterPoint.getBalance().longValue()).isEqualTo(100000L);

        }
    }

    @DisplayName("포인트를 사용할 때,")
    @Nested
    class Using {
        @DisplayName("포인트가 동시에 사용되어도 정상적으로 포인트가 차감된다.")
        @Test
        void successUsed_whenPointIsUsedSimultaneously() throws InterruptedException  {
            //given
            int threadCount = 10;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            User user = userJpaRepository.save(UserFixture.createMember());
            Point point = Point.create(user.getId());
            point.charge(100000);
            point = pointJpaRepository.save(point);

            // 포인트 사용 실패 에러 저장 위치
            List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());

            //when
            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        pointService.using(PointCommand.Using.of(user.getId(), "123456", 10000L));
                    } catch (Exception e) {
                        exceptions.add(e);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            //then
            latch.await();
            Point afterPoint = pointJpaRepository.findById(point.getId()).orElseThrow();
            assertThat(afterPoint.getBalance().longValue()).isEqualTo(0L);

        }
    }
}

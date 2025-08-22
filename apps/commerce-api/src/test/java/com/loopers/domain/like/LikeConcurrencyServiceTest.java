package com.loopers.domain.like;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.infrastructure.like.LikeJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@SpringBootTest
public class LikeConcurrencyServiceTest {

    @MockitoSpyBean
    private LikeJpaRepository likeJpaRepository;
    @Autowired
    private LikeService likeService;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("좋아요를 생성할 때")
    @Nested
    class AddLike {
        @DisplayName("좋아요를 동시에 눌러도, 1개의 좋아요만 생성된다")
        @Test
        void successUsed_whenPointIsUsedSimultaneously() throws InterruptedException  {
            //given
            int threadCount = 10;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            Long userId = 1L;
            Long productId = 1L;
            var command = LikeCommand.Like.of(userId, productId);

            // 포인트 사용 실패 에러 저장 위치
            List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());

            //when
            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        likeService.like(command);
                    } catch (Exception e) {
                        log.warn("좋아요 테스트 실패. command: {}", command, e);
                        exceptions.add(e);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            //then
            latch.await();
            List<Like> foundLike = likeJpaRepository.findAll();
            assertAll(
                    () -> assertThat(foundLike).hasSize(1),
                    () -> assertThat(foundLike.get(0).getUserId()).isEqualTo(userId),
                    () -> assertThat(foundLike.get(0).getProductId()).isEqualTo(productId),
                    () -> assertThat(exceptions).hasSize(0)
            );

        }

        @DisplayName("여러 유저가 좋아요를 동시에 눌러도, 각 유저에 대해서 1개의 좋아요만 생성된다")
        @Test
        void success_whenPointIsUsedSimultaneously() throws InterruptedException  {
            //given
            int threadCount = 10;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            Long userId = 1L;
            Long productId = 1L;

            // 포인트 사용 실패 에러 저장 위치
            List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());

            //when
            for (int i = 0; i < threadCount; i++) {
                final int idx = i;
                executor.submit(() -> {
                    try {
                        // 각 스레드마다 다른 유저 ID를 사용
                        Long id = (long) idx;
                        var command = LikeCommand.Like.of(id, productId);
                        likeService.like(command);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        exceptions.add(e);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            //then
            latch.await();
            List<Like> foundLike = likeJpaRepository.findAll();
            assertAll(
                    () -> assertThat(foundLike).hasSize(10),
                    () -> assertThat(foundLike.get(0).getProductId()).isEqualTo(productId),
                    () -> assertThat(exceptions).hasSize(0)
            );

        }
    }
}

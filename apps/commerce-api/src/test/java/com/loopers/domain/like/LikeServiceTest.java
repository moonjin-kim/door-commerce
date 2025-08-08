package com.loopers.domain.like;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointCommand;
import com.loopers.domain.user.User;
import com.loopers.fixture.UserFixture;
import com.loopers.infrastructure.like.LikeJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
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

@SpringBootTest
class LikeServiceTest {
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
        @DisplayName("UserId와 ProductId에 대해서좋아요가 존재하지 않으면 좋아요를 추가하고 성공 결과를 반환한다.")
        @Test
        void addLike_whenLikeNotExist() {
            //given
            Long userId = 1L;
            Long productId = 1L;
            var command = LikeCommand.Like.of(userId, productId);

            //when
            LikeInfo.LikeResult result = likeService.like(command);

            //then
            boolean foundLike = likeJpaRepository.existsByUserIdAndProductId(command.userId(), command.productId());
            assertAll(
                () -> assertThat(result.isSuccess()).isTrue(),
                () -> assertThat(foundLike).isTrue(),
                ()-> verify(likeJpaRepository, times(1)).save(any(Like.class))
            );
        }

        @DisplayName("UserId와 ProductId에 대해서좋아요가 존재하면 좋아요를 추가하지 않고 좋아요 실패를 반환한다.")
        @Test
        void failLike_whenLikeIsExist() {
            //given
            Long userId = 1L;
            Long productId = 1L;
            var command = LikeCommand.Like.of(userId, productId);
            likeJpaRepository.save(Like.create(command));

            //when
            LikeInfo.LikeResult result = likeService.like(command);

            //then
            assertAll(
                    () -> assertThat(result.isSuccess()).isFalse()
            );
        }

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
                    () -> assertThat(exceptions).hasSize(9)
            );

        }

        @DisplayName("여러 유저가 좋아요를 동시에 눌러도, 각 유저에 대해서 1개의 좋아요만 생성된다")
        @Test
        void success_whenPointIsUsedSimultaneously() throws InterruptedException  {
            //given
            int threadCount = 100;
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
                    () -> assertThat(foundLike).hasSize(100),
                    () -> assertThat(foundLike.get(0).getProductId()).isEqualTo(productId),
                    () -> assertThat(exceptions).hasSize(0)
            );

        }
    }

    @DisplayName("좋아요를 취소할 때")
    @Nested
    class DeleteLike {
        @DisplayName("UserId와 ProductId에 대해서 좋아요가 존재하면 좋아요를 삭제하고 성공 결과를 반환한다.")
        @Test
        void DeleteLike_whenLikeNotExist() {
            //given
            Long userId = 1L;
            Long productId = 1L;
            likeJpaRepository.save(Like.create(LikeCommand.Like.of(userId, productId)));
            LikeCommand.UnLike command = LikeCommand.UnLike.of(userId, productId);

            //when
            LikeInfo.UnLikeResult result = likeService.unlike(command);

            //then
            boolean foundLike = likeJpaRepository.existsByUserIdAndProductId(userId, productId);
            assertAll(
                    () -> assertThat(result.isSuccess()).isTrue(),
                    () -> assertThat(foundLike).isFalse(),
                    ()-> verify(likeJpaRepository, times(1)).deleteByUserIdAndProductId(userId, productId)
            );
        }

        @DisplayName("UserId와 ProductId에 대해서좋아요가 존재하면 좋아요를 추가하지 않고 좋아요 실패를 반환한다.")
        @Test
        void failLike_whenLikeIsExist() {
            //given
            Long userId = 1L;
            Long productId = 1L;
            LikeCommand.UnLike command = LikeCommand.UnLike.of(userId, productId);

            //when
            LikeInfo.UnLikeResult result = likeService.unlike(command);

            //then
            boolean foundLike = likeJpaRepository.existsByUserIdAndProductId(userId, productId);
            assertAll(
                    () -> assertThat(result.isSuccess()).isFalse(),
                    () -> assertThat(foundLike).isFalse(),
                    ()-> verify(likeJpaRepository, times(0)).delete(any(Like.class))
            );
        }
    }

    @DisplayName("유저의 좋아요를 검색할 때,")
    @Nested
    class GetOrdersBy {
        @DisplayName("유저의 좋아요를 검색하면, 좋아요 목록을 반환한다.")
        @Test
        void searchLikes() {
            //given
            Long userId = 1L;
            likeJpaRepository.save(Like.create(LikeCommand.Like.of(userId, 1L)));
            likeJpaRepository.save(Like.create(LikeCommand.Like.of(userId, 2L)));
            likeJpaRepository.save(Like.create(LikeCommand.Like.of(userId, 3L)));
            likeJpaRepository.save(Like.create(LikeCommand.Like.of(userId, 4L)));

            PageRequest<LikeQuery.Search> pageRequest = PageRequest.of(
                    1,10, LikeQuery.Search.of(1L)
            );

            //when
            PageResponse<LikeInfo.Like> result = likeService.search(pageRequest);

            //then
            assertAll(
                () -> assertThat(result.getTotalCount()).isEqualTo(4L),
                () -> assertThat(result.getPage()).isEqualTo(1),
                () -> assertThat(result.getSize()).isEqualTo(10),
                () -> assertThat(result.getItems()).hasSize(4)
            );
        }
    }
}

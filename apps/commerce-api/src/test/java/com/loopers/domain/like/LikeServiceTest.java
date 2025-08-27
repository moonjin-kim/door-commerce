package com.loopers.domain.like;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.interfaces.event.product.ProductEvent;
import com.loopers.infrastructure.comman.CommonApplicationPublisher;
import com.loopers.infrastructure.like.LikeJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.event.RecordApplicationEvents;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@RecordApplicationEvents
class LikeServiceTest {
    @MockitoSpyBean
    private LikeJpaRepository likeJpaRepository;
    @MockitoBean
    private CommonApplicationPublisher eventPublisher;
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
        @DisplayName("UserId와 ProductId에 대해서좋아요가 존재하지 않으면 좋아요를 추가한다.")
        @Test
        void addLike_whenLikeNotExist() {
            //given
            Long userId = 1L;
            Long productId = 1L;
            var command = LikeCommand.Like.of(userId, productId);

            //when
            likeService.like(command);

            //then
            boolean foundLike = likeJpaRepository.existsByUserIdAndProductId(command.userId(), command.productId());
            assertAll(
                () -> assertThat(foundLike).isTrue(),
                ()-> verify(likeJpaRepository, times(1)).save(any(Like.class))
            );
        }

        @DisplayName("UserId와 ProductId에 대해서좋아요가 존재하지 않으면 좋아요 수 추가 이벤트를 발행한다.")
        @Test
        void publishEvent_whenLikeNotExist() {
            //given
            Long userId = 1L;
            Long productId = 1L;
            var command = LikeCommand.Like.of(userId, productId);

            //when
            likeService.like(command);

            //then
            boolean foundLike = likeJpaRepository.existsByUserIdAndProductId(command.userId(), command.productId());
            assertAll(
                    () -> assertThat(foundLike).isTrue(),
                    () -> verify(eventPublisher, times(1)).publish(any(ProductEvent.IncreaseLikeCount.class))
            );
        }

        @DisplayName("UserId와 ProductId에 대해서 좋아요가 존재하면 좋아요를 추가하지 않고 좋아요 실패를 반환한다.")
        @Test
        void failLike_whenLikeIsExist() {
            //given
            Long userId = 1L;
            Long productId = 1L;
            var command = LikeCommand.Like.of(userId, productId);
            likeJpaRepository.save(Like.create(command));

            //when
            likeService.like(command);

            //then
            Long likeCounts = likeJpaRepository.countByProductId(command.productId());
            assertThat(likeCounts).isEqualTo(1L);
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
            likeService.unlike(command);

            //then
            boolean foundLike = likeJpaRepository.existsByUserIdAndProductId(userId, productId);
            assertAll(
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

            //then
            boolean foundLike = likeJpaRepository.existsByUserIdAndProductId(userId, productId);
            assertAll(
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
                () -> assertThat(result.getPage()).isEqualTo(1),
                () -> assertThat(result.getSize()).isEqualTo(10),
                () -> assertThat(result.getItems()).hasSize(4)
            );
        }
    }

    @DisplayName("유저의 좋아요 수를 검색할 때,")
    @Nested
    class getUserLikeCount {
        @DisplayName("유저의 상품 수 검색하면, 좋아요 상품 수 반환한다.")
        @Test
        void searchLikes() {
            //given
            Long userId = 1L;
            likeJpaRepository.save(Like.create(LikeCommand.Like.of(userId, 1L)));
            likeJpaRepository.save(Like.create(LikeCommand.Like.of(userId, 2L)));
            likeJpaRepository.save(Like.create(LikeCommand.Like.of(userId, 3L)));
            likeJpaRepository.save(Like.create(LikeCommand.Like.of(userId, 4L)));

            //when
            Long result = likeService.getUserLikeCount(LikeQuery.SearchCount.of(userId));

            //then
            assertThat(result).isEqualTo(4);
        }
    }
}

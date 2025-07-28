package com.loopers.domain.like;

import com.loopers.infrastructure.like.LikeJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class ProductLikeServiceTest {
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
    class AddProductLike {
        @DisplayName("UserId와 ProductId에 대해서좋아요가 존재하지 않으면 좋아요를 추가하고 성공 결과를 반환한다.")
        @Test
        void addLike_whenLikeNotExist() {
            //given
            Long userId = 1L;
            Long productId = 1L;

            //when
            LikeInfo.AddLikeResult result = likeService.addLike(userId, productId);

            //then
            boolean foundLike = likeJpaRepository.existsByUserIdAndProductId(userId, productId);
            assertAll(
                () -> assertThat(result.isSuccess()).isTrue(),
                () -> assertThat(foundLike).isTrue(),
                ()-> verify(likeJpaRepository, times(1)).save(any(ProductLike.class))
            );
        }

        @DisplayName("UserId와 ProductId에 대해서좋아요가 존재하면 좋아요를 추가하지 않고 좋아요 실패를 반환한다.")
        @Test
        void failLike_whenLikeIsExist() {
            //given
            Long userId = 1L;
            Long productId = 1L;
            likeJpaRepository.save(ProductLike.create(userId, productId));

            //when
            LikeInfo.AddLikeResult result = likeService.addLike(userId, productId);

            //then
            assertAll(
                    () -> assertThat(result.isSuccess()).isFalse()
            );
        }
    }

    @DisplayName("좋아요를 취소할 때")
    @Nested
    class DeleteProductLike {
        @DisplayName("UserId와 ProductId에 대해서 좋아요가 존재하면 좋아요를 삭제하고 성공 결과를 반환한다.")
        @Test
        void DeleteLike_whenLikeNotExist() {
            //given
            Long userId = 1L;
            Long productId = 1L;
            likeJpaRepository.save(ProductLike.create(userId, productId));

            //when
            LikeInfo.DeleteLikeResult result = likeService.unlike(userId, productId);

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

            //when
            LikeInfo.DeleteLikeResult result = likeService.unlike(userId, productId);

            //then
            boolean foundLike = likeJpaRepository.existsByUserIdAndProductId(userId, productId);
            assertAll(
                    () -> assertThat(result.isSuccess()).isFalse(),
                    () -> assertThat(foundLike).isFalse(),
                    ()-> verify(likeJpaRepository, times(0)).delete(any(ProductLike.class))
            );
        }
    }

    @DisplayName("유저의 좋아요를 검색할 때,")
    @Nested
    class Search {
        @DisplayName("유저의 좋아요를 검색하면, 좋아요 목록을 반환한다.")
        @Test
        void searchLikes() {
            //given
            Long userId = 1L;
            likeJpaRepository.save(ProductLike.create(userId, 1L));
            likeJpaRepository.save(ProductLike.create(userId, 2L));
            likeJpaRepository.save(ProductLike.create(userId, 3L));
            likeJpaRepository.save(ProductLike.create(userId, 4L));

            //when
            LikeInfo.SearchResult result = likeService.search(LikeQuery.Search.of(10,0, userId));

            //then
            assertAll(
                () -> assertThat(result.totalCount()).isEqualTo(4),
                () -> assertThat(result.limit()).isEqualTo(10),
                () -> assertThat(result.offset()).isEqualTo(0),
                () -> assertThat(result.likes()).hasSize(4)
            );
        }
    }
}

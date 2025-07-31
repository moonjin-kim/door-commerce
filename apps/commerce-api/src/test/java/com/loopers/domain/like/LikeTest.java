package com.loopers.domain.like;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LikeTest {
    @DisplayName("좋아요를 생성할 때,")
    @Nested
    class create {
        @DisplayName("사용자 ID가 null이면 BAD_REQUEST에외를 발생시킨다.")
        @org.junit.jupiter.api.Test
        void userIdIsNull() {
            // given

            // when
            CoreException result = assertThrows(CoreException.class, () -> Like.create(new LikeCommand.Like(null, 1L)));

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("상품 ID가 null이면 BAD_REQUEST예외를 발생시킨다.")
        @org.junit.jupiter.api.Test
        void productIdIsNull() {
            // given

            // when
            CoreException result = assertThrows(CoreException.class, () -> Like.create(new LikeCommand.Like(1L, null)));

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("생성 값이 정상적으로 들어오면, 정상적으로 좋아요를 생성한다.")
        @org.junit.jupiter.api.Test
        void success() {
            // given

            // when
            Like like = Like.create(new LikeCommand.Like(1L, 1L));
            //then
            assertNotNull(like);
            assertEquals(1L, like.getUserId());
            assertEquals(1L, like.getProductId());
        }
    }
}

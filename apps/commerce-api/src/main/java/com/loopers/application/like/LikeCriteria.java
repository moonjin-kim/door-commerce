package com.loopers.application.like;

import com.loopers.domain.like.LikeCommand;

public class LikeCriteria {
    public record Like(
            Long userId,
            Long productId
    ) {
        public static Like of(Long userId, Long productId) {
            return new Like(userId, productId);
        }

        public LikeCommand.Like toCommand() {
            return LikeCommand.Like.of(userId, productId);
        }
    }

    public record UnLike(
            Long userId,
            Long productId
    ) {
        public static UnLike of(Long userId, Long productId) {
            return new UnLike(userId, productId);
        }

        public LikeCommand.UnLike toCommand() {
            return LikeCommand.UnLike.of(userId, productId);
        }
    }
}

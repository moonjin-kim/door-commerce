package com.loopers.domain.like;

public class LikeCommand {
    public record Like(
            Long userId,
            Long productId
    ) {
        public static Like of(Long userId, Long productId) {
            return new Like(userId, productId);
        }
    }

    public record UnLike(
            Long userId,
            Long productId
    ) {
        public static UnLike of(Long userId, Long productId) {
            return new UnLike(userId, productId);
        }
    }
}

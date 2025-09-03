package com.loopers.domain.like;

public class LikeEvent {
    public record Like(Long productId, Long userId) {
        public static Like of(Long productId, Long userId) {
            return new Like(productId, userId);
        }
    }

    public record UnLike(Long productId, Long userId) {
        public static UnLike of(Long productId, Long userId) {
            return new UnLike(productId, userId);
        }
    }
}

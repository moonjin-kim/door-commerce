package com.loopers.domain.like;

public class LikeEvent {
    public record Like(Long productId) {
        public static Like of(Long productId) {
            return new Like(productId);
        }
    }

    public record UnLike(Long productId) {
        public static UnLike of(Long productId) {
            return new UnLike(productId);
        }
    }
}

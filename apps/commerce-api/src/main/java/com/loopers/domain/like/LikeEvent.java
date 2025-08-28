package com.loopers.domain.like;

public class LikeEvent {
    public record AddLike(Long productId) {
        public static AddLike of(Long productId) {
            return new AddLike(productId);
        }
    }

    public record CancelLike(Long productId) {
        public static CancelLike of(Long productId) {
            return new CancelLike(productId);
        }
    }
}

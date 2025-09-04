package com.loopers.infrastructure.outbound;

public class LikeMessage {
    public static final class TOPIC {
        public static final String LIKE = "product.likeChange";
    }

    public class V1 {
        public static final String VERSION = "v1";

        public static final class Type {
            public static final String CHANGED = "LIKE_CHANGED:V1";
        }

        public record Changed(Long productId, Long userId, Long delta) {
            public static Changed like(Long productId, Long userId) {
                return new Changed(productId, userId, 1L);
            }

            public static Changed unlike(Long productId, Long userId) {
                return new Changed(productId, userId, -1L);
            }
        }
    }
}

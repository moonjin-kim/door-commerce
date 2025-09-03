package com.loopers.interfaces.event.kafka;

public class LikeMessage {
    public class V1 {
        public static final String VERSION = "v1";

        public static final class TOPIC {
            public static final String LIKE = "product.likeChange";
        }

        public static final class Type {
            public static final String LIKE = "LIKE_CHANGED_V1";
        }

        public record Changed(Long productId, Long userId, Long delta) {
            static Changed like(Long productId, Long userId) {
                return new Changed(productId, userId, 1L);
            }

            static Changed unlike(Long productId, Long userId) {
                return new Changed(productId, userId, -1L);
            }
        }
    }
}

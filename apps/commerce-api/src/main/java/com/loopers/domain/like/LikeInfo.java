package com.loopers.domain.like;

public class LikeInfo {
    public record LikeResult(
            boolean isSuccess
    ) {
        public static LikeResult fail() {
            return new LikeResult(false);
        }

        public static LikeResult success() {
            return new LikeResult(true);
        }
    }

    public record UnLikeResult(
            boolean isSuccess
    ) {

        public static UnLikeResult fail() {
            return new UnLikeResult(false);
        }

        public static UnLikeResult success() {
            return new UnLikeResult(true);
        }
    }

    public record Like(
            long userId,
            long productId
    ) {
        public static Like of(ProductLike productLike) {
            return new Like(productLike.getUserId(), productLike.getProductId());
        }
    }
}

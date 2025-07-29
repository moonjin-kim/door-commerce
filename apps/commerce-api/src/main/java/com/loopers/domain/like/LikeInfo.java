package com.loopers.domain.like;

import com.loopers.domain.product.Product;

import java.util.List;

public class LikeInfo {
    public record AddLikeResult(
            boolean isSuccess
    ) {
        public static AddLikeResult fail() {
            return new AddLikeResult(false);
        }

        public static AddLikeResult success() {
            return new AddLikeResult(true);
        }
    }

    public record DeleteLikeResult(
            boolean isSuccess
    ) {

        public static DeleteLikeResult fail() {
            return new DeleteLikeResult(false);
        }

        public static DeleteLikeResult success() {
            return new DeleteLikeResult(true);
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

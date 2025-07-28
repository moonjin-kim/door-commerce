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

    public record SearchResult(
            long limit,
            long offset,

            long totalCount,
            List<ProductLike> likes
    ) {
        public static SearchResult of(long totalCount, int limit, long offset, List<ProductLike> likes) {
            return new SearchResult(totalCount, limit, offset, likes);
        }
    }
}

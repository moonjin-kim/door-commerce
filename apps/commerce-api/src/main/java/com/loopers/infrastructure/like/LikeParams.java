package com.loopers.infrastructure.like;

import com.loopers.infrastructure.product.ProductParams;

public class LikeParams {
    public record Search(
            int limit,
            long offset,
            long userId
    ) {
        public static LikeParams.Search of(int limit, long offset, long userId) {
            return new LikeParams.Search(limit, offset, userId);
        }
    }
}

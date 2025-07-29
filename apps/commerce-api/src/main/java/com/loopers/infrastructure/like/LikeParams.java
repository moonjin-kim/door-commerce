package com.loopers.infrastructure.like;

import com.loopers.infrastructure.product.ProductParams;

public class LikeParams {
    public record Search(
            long userId
    ) {
        public static LikeParams.Search of(long userId) {
            return new LikeParams.Search(userId);
        }
    }
}

package com.loopers.domain.like;

import com.loopers.infrastructure.like.LikeParams;

public class LikeQuery {
    public record Search(
            long userId
    ) {
        public static LikeQuery.Search of(long userId) {
            return new LikeQuery.Search(userId);
        }

        public LikeParams.Search toParams() {
            return LikeParams.Search.of(userId);
        }
    }

    public record SearchCount(
            long userId
    ) {
        public static LikeQuery.SearchCount of(long userId) {
            return new LikeQuery.SearchCount(userId);
        }
    }
}

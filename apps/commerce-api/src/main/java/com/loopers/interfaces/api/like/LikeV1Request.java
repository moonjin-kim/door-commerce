package com.loopers.interfaces.api.like;

import com.loopers.domain.like.LikeQuery;

public class LikeV1Request {
    public record Search(
            int page,
            int size
    ) {
        public static LikeV1Request.Search of(int page, int size) {
            return new LikeV1Request.Search(page, size);
        }

        public LikeQuery.Search toQuery(long userId) {
            return LikeQuery.Search.of(
                    userId
            );
        }

        private Long getOffset() {
            return (long) (page-1) * size;
        }
    }
}

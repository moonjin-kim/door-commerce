package com.loopers.application.like;

import com.loopers.domain.like.LikeCommand;
import com.loopers.domain.like.LikeQuery;
import com.loopers.infrastructure.like.LikeParams;

public class LikeCriteria {
    public record Like(
            Long userId,
            Long productId
    ) {
        public static Like of(Long userId, Long productId) {
            return new Like(userId, productId);
        }

        public LikeCommand.Like toCommand() {
            return LikeCommand.Like.of(userId, productId);
        }
    }

    public record UnLike(
            Long userId,
            Long productId
    ) {
        public static UnLike of(Long userId, Long productId) {
            return new UnLike(userId, productId);
        }

        public LikeCommand.UnLike toCommand() {
            return LikeCommand.UnLike.of(userId, productId);
        }
    }

    public record Search(
            long userId
    ) {
        public static Search of(long userId) {
            return new Search(userId);
        }

        public LikeQuery.Search toQuery() {
            return LikeQuery.Search.of(userId);
        }
    }

    public record SearchCount(
            long userId
    ) {
        public static SearchCount of(long userId) {
            return new SearchCount(userId);
        }

        public LikeQuery.SearchCount toQuery() {
            return LikeQuery.SearchCount.of(userId);
        }
    }
}

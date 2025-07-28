package com.loopers.domain.like;

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
}

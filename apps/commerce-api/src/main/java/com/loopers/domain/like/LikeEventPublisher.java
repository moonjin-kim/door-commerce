package com.loopers.domain.like;

public interface LikeEventPublisher {
    void publish(LikeEvent.Like event);
    void publish(LikeEvent.UnLike event);
}

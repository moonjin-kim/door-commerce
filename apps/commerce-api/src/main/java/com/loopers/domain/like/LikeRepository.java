package com.loopers.domain.like;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.infrastructure.like.LikeParams;

public interface LikeRepository {
    boolean existsBy(Long userId, Long productId);

    Like save(Like like);

    PageResponse<Like> search(PageRequest<LikeParams.Search> likeSearch);

    Long countBy(Long productId);

    void delete(Long userId, Long productId);
}

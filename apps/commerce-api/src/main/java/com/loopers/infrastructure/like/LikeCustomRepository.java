package com.loopers.infrastructure.like;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.domain.like.Like;


public interface LikeCustomRepository {

    public PageResponse<Like> search(PageRequest<LikeParams.Search> likeSearch);
}

package com.loopers.infrastructure.like;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.domain.like.LikeInfo;
import com.loopers.domain.like.ProductLike;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


public interface LikeCustomRepository {

    public PageResponse<ProductLike> search(PageRequest<LikeParams.Search> likeSearch);
}

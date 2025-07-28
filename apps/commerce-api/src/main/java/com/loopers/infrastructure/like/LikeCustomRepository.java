package com.loopers.infrastructure.like;

import com.loopers.domain.like.LikeInfo;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


public interface LikeCustomRepository {

    public LikeInfo.SearchResult search(LikeParams.Search likeParams);
}

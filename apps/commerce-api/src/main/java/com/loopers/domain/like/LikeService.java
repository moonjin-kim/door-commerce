package com.loopers.domain.like;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.infrastructure.like.LikeParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;

    public PageResponse<LikeInfo.Like> search(PageRequest<LikeQuery.Search> query) {
        PageResponse<ProductLike> pageRequest = likeRepository.search(query.map(LikeQuery.Search::toParams));

        return pageRequest.map(LikeInfo.Like::of);
    }

    @Transactional
    public LikeInfo.AddLikeResult like(LikeCommand.Like command) {
        if (likeRepository.existsBy(command.userId(), command.productId())) {
            return LikeInfo.AddLikeResult.fail();
        }

        likeRepository.save(ProductLike.create(command));
        return LikeInfo.AddLikeResult.success();
    }

    @Transactional
    public LikeInfo.DeleteLikeResult unlike(Long userId, Long productId) {
        if (likeRepository.existsBy(userId, productId)) {
            likeRepository.delete(userId, productId);
            return LikeInfo.DeleteLikeResult.success();
        }

        return LikeInfo.DeleteLikeResult.fail();
    }
}

package com.loopers.domain.like;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
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
    public LikeInfo.LikeResult like(LikeCommand.Like command) {
        if (likeRepository.existsBy(command.userId(), command.productId())) {
            return LikeInfo.LikeResult.fail();
        }

        likeRepository.save(ProductLike.create(command));
        return LikeInfo.LikeResult.success();
    }

    @Transactional
    public LikeInfo.UnLikeResult unlike(LikeCommand.UnLike command) {
        if (likeRepository.existsBy(command.userId(), command.productId())) {
            likeRepository.delete(command.userId(), command.productId());
            return LikeInfo.UnLikeResult.success();
        }

        return LikeInfo.UnLikeResult.fail();
    }
}

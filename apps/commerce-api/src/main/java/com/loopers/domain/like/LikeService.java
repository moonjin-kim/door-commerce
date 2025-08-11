package com.loopers.domain.like;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.RollbackException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.tool.schema.spi.CommandAcceptanceException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;

    public PageResponse<LikeInfo.Like> search(PageRequest<LikeQuery.Search> query) {
        PageResponse<Like> pageRequest = likeRepository.search(query.map(LikeQuery.Search::toParams));

        return pageRequest.map(LikeInfo.Like::of);
    }

    // 예시: 만약 PersistenceException이 발생했다면
//    @Transactional(noRollbackFor = DataIntegrityViolationException.class)
    public LikeInfo.LikeResult like(LikeCommand.Like command) {
        if (likeRepository.existsBy(command.userId(), command.productId())) {
            return LikeInfo.LikeResult.fail();
        }

        try {
            likeRepository.save(Like.create(command));
            return LikeInfo.LikeResult.success();
        } catch (DataIntegrityViolationException e) {
            // 어떤 예외가 잡혔는지 로그로 확인 (2단계 진단을 위해)
            log.warn("데이터 중복으로 '좋아요' 저장 실패. command: {}", command, e);
            return LikeInfo.LikeResult.success();
        }
    }

    public LikeInfo.IsLiked isLiked(LikeCommand.IsLiked command) {
        boolean exists = likeRepository.existsBy(command.userId(), command.productId());

        return LikeInfo.IsLiked.of(exists);
    }

    @Transactional
    public LikeInfo.UnLikeResult unlike(LikeCommand.UnLike command) {
        if (likeRepository.existsBy(command.userId(), command.productId())) {
            likeRepository.delete(command.userId(), command.productId());
            return LikeInfo.UnLikeResult.success();
        }

        return LikeInfo.UnLikeResult.fail();
    }

    @Transactional(readOnly = true)
    public LikeInfo.GetLikeCount getLikeCount(Long productId) {
        Long count = likeRepository.countBy(productId);
        return LikeInfo.GetLikeCount.of(count);

    }
}

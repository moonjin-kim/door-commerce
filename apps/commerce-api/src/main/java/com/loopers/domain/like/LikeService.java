package com.loopers.domain.like;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    // 예시: 만약 PersistenceException이 발생했다면
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
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
            return LikeInfo.LikeResult.fail();
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
        Object countObject = redisTemplate.opsForValue().get("likeCount:" + productId);

        if (countObject != null) {
            // 1. 안전하게 Number 타입으로 캐스팅합니다.
            // 2. longValue() 메서드를 호출하여 Long 타입으로 변환합니다.
            long likeCount = ((Number) countObject).longValue();
            return LikeInfo.GetLikeCount.of(likeCount);
        }

        Long count = likeRepository.countBy(productId);
        redisTemplate.opsForValue().set("likeCount:" + productId, count, Duration.ofMinutes(1));
        return LikeInfo.GetLikeCount.of(count);

    }

    public PageResponse<LikeInfo.Like> search(PageRequest<LikeQuery.Search> query) {
        PageResponse<Like> pageRequest = likeRepository.search(query.map(LikeQuery.Search::toParams));

        return pageRequest.map(LikeInfo.Like::of);
    }

    public Long getUserLikeCount(LikeQuery.SearchCount query) {
        return likeRepository.countByUserId(query.userId());
    }
}

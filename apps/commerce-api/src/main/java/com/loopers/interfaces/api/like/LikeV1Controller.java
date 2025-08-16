package com.loopers.interfaces.api.like;

import com.loopers.application.like.LikeCriteria;
import com.loopers.application.like.LikeFacade;
import com.loopers.application.like.LikeResult;
import com.loopers.application.product.ProductCriteria;
import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.domain.like.LikeQuery;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.product.ProductV1Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/like")
public class LikeV1Controller implements LikeV1ApiSpec{
    private final LikeFacade likeFacade;

    @PostMapping("/products/{productId}")
    @Override
    public ApiResponse<String> like(
            @PathVariable("productId") Long productId,
            @RequestHeader("X-USER-ID") Long userId
    ) {
        return ApiResponse.success(likeFacade.like(LikeCriteria.Like.of(productId, userId)));
    }

    @DeleteMapping("/products/{productId}")
    @Override
    public ApiResponse<String> unlike(
            @PathVariable("productId") Long productId,
            @RequestHeader("X-USER-ID") Long userId
    ) {
        return ApiResponse.success(likeFacade.unLike(LikeCriteria.UnLike.of(productId, userId)));
    }

    @GetMapping("/products")
    @Override
    public ApiResponse<PageResponse<LikeV1Response.LikeProduct>> findAllBy(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestHeader("X-USER-ID") Long userId
    ) {
        PageRequest<LikeCriteria.Search> searchCriteria = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                LikeCriteria.Search.of(userId)
        );
        PageResponse<LikeResult.LikeProduct> likeProducts = likeFacade.search(
                searchCriteria
        );

        return ApiResponse.success(
                likeProducts.map(LikeV1Response.LikeProduct::of)
        );
    }

    @GetMapping("/products/count")
    @Override
    public ApiResponse<LikeV1Response.LikeProductCount> countBy(
            @RequestHeader("X-USER-ID") Long userId
    ) {
        LikeResult.LikeProductCount searchCount = likeFacade.searchCount(
                LikeCriteria.SearchCount.of(userId)
        );

        return ApiResponse.success(LikeV1Response.LikeProductCount.of(searchCount)
        );
    }
}

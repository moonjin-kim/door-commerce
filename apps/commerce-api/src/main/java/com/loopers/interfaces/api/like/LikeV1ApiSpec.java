package com.loopers.interfaces.api.like;

import com.loopers.domain.PageResponse;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.brand.BrandV1Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Like V1 API", description = "Like API 입니다.")
public interface LikeV1ApiSpec {
    @Operation(
            summary = "좋아요",
            description = "ID로 상품을 좋아요"
    )
    ApiResponse<String> like(Long productId, Long userId);

    @Operation(
            summary = "좋아요 취소",
            description = "ID로 상품을 좋아요 취소"
    )
    ApiResponse<String> unlike(Long productId, Long userId);

    @Operation(
            summary = "좋아요 조회",
            description = "Page형식으로 좋아요한 상품들을 조회합니다."
    )
    ApiResponse<PageResponse<LikeV1Response.LikeProduct>> findAllBy(
            Pageable pageable,
            Long userId
    );
}

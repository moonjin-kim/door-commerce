package com.loopers.interfaces.api.product;

import org.springframework.data.domain.Pageable;
import com.loopers.domain.PageResponse;
import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.ModelAttribute;

@Tag(name = "Product V1 API", description = "Product API 입니다.")
public interface ProductV1ApiSpec {

    @Operation(
            summary = "상품 상세 조회",
            description = "상품 id로 상품을을 조회한다"
    )
    ApiResponse<ProductV1Response.ProductDetail> getBy(Long userId);


    @Operation(
            summary = "상품 목록 조회",
            description = "상품 목록을 조회한다. 페이지네이션이 적용되어 있다."
    )
    ApiResponse<PageResponse<ProductV1Response.ProductSummary>> getList(@PageableDefault(size = 10) Pageable pageable,
                                                                        @ModelAttribute ProductV1Request.Search searchDto);

    @Operation(
            summary = "상품 좋아요",
            description = "상품을 좋아요한다."
    )
    ApiResponse<String> like(Long userId, Long productId);

    @Operation(
            summary = "상품 좋아요 취소",
            description = "상품 좋아요를 취소한다."
    )
    ApiResponse<String> unLike(Long userId, Long productId);
}

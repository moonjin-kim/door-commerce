package com.loopers.interfaces.api.product;

import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.PageResponse;
import com.loopers.interfaces.api.point.PointV1Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.awt.print.Pageable;

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
    ApiResponse<PageResponse<ProductV1Response.ProductDetail>> getList(ProductV1Request.Search search, Pageable pageable);
}

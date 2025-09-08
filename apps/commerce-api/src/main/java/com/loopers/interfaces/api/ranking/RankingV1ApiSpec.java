package com.loopers.interfaces.api.ranking;

import com.loopers.domain.PageResponse;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.product.ProductV1Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;

@Tag(name = "Ranking V1 API", description = "Ranking API 입니다.")
public interface RankingV1ApiSpec {
    @Operation(
            summary = "랭킹조회 조회",
            description = "오늘 랭킹을 조회한다"
    )
    ApiResponse<PageResponse<RankingV1Response.ProductDto>> getRanking(Pageable pageable, RankingV1Request.GetRanking request);
}

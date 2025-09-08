package com.loopers.interfaces.api.ranking;

import com.loopers.application.ranking.RankingFacade;
import com.loopers.domain.PageResponse;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.product.ProductV1Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ranking")
public class RankingController implements RankingV1ApiSpec{
    private final RankingFacade rankingFacade;

    @GetMapping("")
    @Override
    public ApiResponse<PageResponse<RankingV1Response.ProductDto>> getRanking(Pageable pageable, RankingV1Request.GetRanking request) {
        return ApiResponse.success(
                rankingFacade.getRanking(request.date(), pageable.getPageNumber(), pageable.getPageSize())
        );
    }
}

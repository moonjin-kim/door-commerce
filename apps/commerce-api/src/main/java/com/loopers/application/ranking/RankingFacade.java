package com.loopers.application.ranking;

import com.loopers.domain.PageResponse;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.ranking.RankingCommand;
import com.loopers.domain.ranking.RankingService;
import com.loopers.interfaces.api.ranking.RankingV1Request;
import com.loopers.interfaces.api.ranking.RankingV1Response;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RankingFacade {
    private final RankingService rankingService;
    private final ProductService productService;

    public PageResponse<RankingV1Response.ProductDto> getRanking(String date, int page, int size) {
        List<String> rankingPage = rankingService.getRanking(RankingCommand.GetRanking.of(date, page, size)).stream().toList();

        List<RankingV1Response.ProductDto> products = rankingPage.stream()
                .map(Long::valueOf)
                .map(productId -> productService.getBy(productId)
                        .map(RankingV1Response.ProductDto::from)
                        .orElse(null)
                )
                .filter(Objects::nonNull)
                .toList();

        return PageResponse.of(page,size, products);
    }
}

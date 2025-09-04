package com.loopers.application.like;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.domain.like.LikeInfo;
import com.loopers.domain.like.LikeQuery;
import com.loopers.domain.like.LikeService;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class LikeFacade {

    private final LikeService likeService;
    private final ProductService productService;

    @Transactional
    public String like(LikeCriteria.Like like) {
        productService.getBy(like.productId()).orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품입니다."));

        likeService.like(like.toCommand());
        return "좋아요에 성공했습니다";
    }


    @Transactional
    public String unLike(LikeCriteria.UnLike like) {
        productService.getBy(like.productId()).orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품입니다."));

        likeService.unlike(like.toCommand());
        return "좋아요에 성공했습니다";
    }

    public PageResponse<LikeResult.LikeProduct> search(PageRequest<LikeCriteria.Search> query) {
        PageResponse<LikeInfo.Like> searchResult = likeService.search(query.map(LikeCriteria.Search::toQuery));

        List<LikeResult.LikeProduct> likeProducts = searchResult.getItems().stream()
                .map(like -> {
                    Product product = productService.getBy(like.productId())
                            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품입니다."));
                    return LikeResult.LikeProduct.of(product);
                })
                .toList();

        return PageResponse.of(
                searchResult.getPage(),
                searchResult.getSize(),
                likeProducts
        );
    }

    public LikeResult.LikeProductCount searchCount(LikeCriteria.SearchCount query) {
        Long searchResult = likeService.getUserLikeCount(query.toQuery());

        return LikeResult.LikeProductCount.of(searchResult);
    }
}

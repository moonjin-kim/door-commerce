package com.loopers.application.product;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.like.LikeCommand;
import com.loopers.domain.like.LikeInfo;
import com.loopers.domain.like.LikeService;
import com.loopers.domain.product.*;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Transactional
public class ProductFacade {
    private final ProductService productService;
    private final BrandService brandService;
    private final LikeService likeService;

    public ProductResult.ProductDetail getBy(Long productId, Long userId) {
        Product product = productService.getBy(productId).orElseThrow(() -> {
            throw new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품입니다.");
        });

        Brand brand = brandService.getBy(product.getId()).orElseThrow(() -> {
            throw new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 브랜드입니다.");
        });

        // 유저의 좋아요 여부 조회
        LikeInfo.IsLiked likeInfo = likeService.isLiked(
                LikeCommand.IsLiked.of(userId, productId)
        );

        LikeInfo.GetLikeCount likeCount = likeService.getLikeCount(productId);

        return ProductResult.ProductDetail.from(product, brand, likeInfo.isLiked(), likeCount.count());
    }


    public PageResponse<ProductResult.ProductDto> search(PageRequest<ProductCriteria.Search> criteria) {
        PageRequest<ProductCommand.Search> searchCommand = criteria.map(ProductCriteria.Search::toCommand);

        PageResponse<ProductView> productPage = productService.search(searchCommand);

        return productPage.map(ProductResult.ProductDto::from);
    }

}

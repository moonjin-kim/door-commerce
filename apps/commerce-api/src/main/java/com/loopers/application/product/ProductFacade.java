package com.loopers.application.product;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.domain.brand.BrandInfo;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.like.LikeCommand;
import com.loopers.domain.like.LikeInfo;
import com.loopers.domain.like.LikeService;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.product.ProductView;
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
        ProductInfo product = productService.getBy(productId);

        BrandInfo brandInfo = brandService.findBy(product.brandId());

        LikeInfo.IsLiked likeInfo = likeService.isLiked(
                LikeCommand.IsLiked.of(userId, productId)
        );

        LikeInfo.GetLikeCount likeCount = likeService.getLikeCount(productId);

        return ProductResult.ProductDetail.from(product, brandInfo, likeInfo.isLiked(), likeCount.count());
    }


    public PageResponse<ProductResult.ProductDto> search(PageRequest<ProductCriteria.Search> criteria) {
        PageRequest<ProductCommand.Search> searchCommand = criteria.map(ProductCriteria.Search::toCommand);

        PageResponse<ProductView> productPage = productService.search(searchCommand);

        return productPage.map(ProductResult.ProductDto::from);
    }

}

package com.loopers.application.like;

import com.loopers.domain.like.LikeInfo;
import com.loopers.domain.like.LikeQuery;
import com.loopers.domain.like.LikeService;
import com.loopers.domain.like.ProductLike;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LikeFacade {

    private final LikeService likeService;
    private final ProductService productService;

    public LikeResult.Search search(LikeQuery.Search query) {
        LikeInfo.SearchResult searchResult = likeService.search(query);

        List<Long> productIds = searchResult.likes().stream()
                .map(ProductLike::getProductId)
                .collect(Collectors.toList());

        // 3. 추출된 productId 리스트를 사용하여 'Product' 목록을 한 번에 조회합니다.
        List<Product> products = productService.findAllBy(productIds);

        // 4. 조회된 Product 목록을 Map<productId, Product> 형태로 변환하여 검색 효율을 높입니다.
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        List<LikeResult.LikeProduct> likeProducts = searchResult.likes().stream()
                .map(like -> {
                    Product product = productMap.get(like.getProductId());
                    return LikeResult.LikeProduct.of(product);
                })
                .toList();

        return LikeResult.Search.of(searchResult, likeProducts);
    }
}

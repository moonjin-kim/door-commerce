package com.loopers.application.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.config.redis.RedisConfig;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Component
@Transactional
public class ProductFacade {
    private final ProductService productService;
    private final BrandService brandService;
    private final LikeService likeService;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public ProductResult.ProductDetail getBy(Long productId, Long userId) {
        final String cacheKey = "product::" + productId;
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        // 1. [수정] 캐시에서 JSON 문자열을 조회합니다.
        String cachedJson = valueOperations.get(cacheKey);
        if (cachedJson != null) {
            try {
                // 2. [수정] JSON 문자열을 ProductResult.ProductDetail 객체로 변환합니다.
                return objectMapper.readValue(cachedJson, ProductResult.ProductDetail.class);
            } catch (JsonProcessingException e) {
                log.error("캐시된 JSON을 파싱하는 데 실패했습니다. key: {}", cacheKey, e);
                // 파싱 실패 시 캐시를 무시하고 DB에서 조회하도록 로직을 이어갑니다.
            }
        }

        Product product = productService.getBy(productId).orElseThrow(() -> {
            throw new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품입니다.");
        });

        Brand brand = brandService.getBy(product.getBrandId()).orElseThrow(() -> {
            throw new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 브랜드입니다.");
        });

        // 유저의 좋아요 여부 조회
        LikeInfo.IsLiked likeInfo = likeService.isLiked(
                LikeCommand.IsLiked.of(userId, productId)
        );

        LikeInfo.GetLikeCount likeCount = likeService.getLikeCount(productId);

        ProductResult.ProductDetail productDetail = ProductResult.ProductDetail.of(
                product,
                brand,
                likeInfo.isLiked(),
                likeCount.count()
        );

        try {
            // 3. [수정] 객체를 JSON 문자열로 변환하여 캐시에 저장합니다.
            String jsonValue = objectMapper.writeValueAsString(productDetail);
            valueOperations.set(cacheKey, jsonValue, Duration.ofMinutes(10));
        } catch (JsonProcessingException e) {
            log.error("객체를 JSON으로 변환하여 캐시에 저장하는 데 실패했습니다. key: {}", cacheKey, e);
        }

        return productDetail;
    }


    public PageResponse<ProductResult.ProductDto> search(PageRequest<ProductCriteria.Search> criteria) {
        PageRequest<ProductCommand.Search> searchCommand = criteria.map(ProductCriteria.Search::toCommand);

        PageResponse<ProductView> productPage = productService.search(searchCommand);

        return productPage.map(ProductResult.ProductDto::from);
    }

    public ProductResult.SearchCount searchCount(ProductCriteria.SearchCount criteria) {
        Long count = productService.searchCount(criteria.toCommand());

        return new ProductResult.SearchCount(count);
    }

}

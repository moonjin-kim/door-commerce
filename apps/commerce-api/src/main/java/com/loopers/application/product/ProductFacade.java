package com.loopers.application.product;

import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductQuery;
import com.loopers.domain.product.ProductService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProductFacade {
    private final ProductService productService;

    public ProductResult.ProductDto getBy(Long productId) {
        Product product = productService.getBy(productId).orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "[productId = " + productId + "] 존재하지 않는 상품입니다."));

        return ProductResult.ProductDto.of(product);
    }

    public ProductResult.ProductPage search(ProductQuery.Search query) {
        ProductInfo.ProductPage productPage = productService.search(query);

        return ProductResult.ProductPage.of(productPage);
    }

}

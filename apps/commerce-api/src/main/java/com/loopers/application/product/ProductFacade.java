package com.loopers.application.product;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProductFacade {
    private final ProductService productService;

    public ProductResult.ProductDto getBy(Long productId) {
        ProductInfo product = productService.getBy(productId);

        return ProductResult.ProductDto.of(product);
    }


    public PageResponse<ProductResult.ProductDto> search(PageRequest<ProductCriteria.Search> criteria) {
        PageRequest<ProductCommand.Search> searchCommand = criteria.map(ProductCriteria.Search::toCommand);

        PageResponse<ProductInfo> productPage = productService.search(searchCommand);

        return productPage.map(ProductResult.ProductDto::of);
    }

}

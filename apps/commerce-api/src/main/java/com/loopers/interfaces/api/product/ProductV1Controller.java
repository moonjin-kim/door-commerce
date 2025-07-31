package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductCriteria;
import com.loopers.application.product.ProductFacade;
import com.loopers.application.product.ProductResult;
import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductV1Controller implements ProductV1ApiSpec{
    private final ProductFacade productFacade;

    @GetMapping("")
    @Override
    public ApiResponse<PageResponse<ProductV1Response.ProductSummary>> getList(
            @PageableDefault(size = 10) Pageable pageable,
            @ModelAttribute ProductV1Request.Search searchDto
    ) {
        PageRequest<ProductCriteria.Search> searchCriteria = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                searchDto.toCriteria()
        );

        PageResponse<ProductResult.ProductDto> productPage = productFacade.search(
                searchCriteria
        );

        return ApiResponse.success(
                productPage.map(
                        ProductV1Response.ProductSummary::of
                )
        );
    }

    @GetMapping("/{productId}")
    @Override
    public ApiResponse<ProductV1Response.ProductDetail> getBy(@PathVariable(value = "productId")Long brandId) {
        ProductResult.ProductDto product = productFacade.getBy(brandId);

        return ApiResponse.success(
                ProductV1Response.ProductDetail.of(product)
        );
    }
}

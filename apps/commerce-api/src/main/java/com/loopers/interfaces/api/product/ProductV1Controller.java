package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductFacade;
import com.loopers.application.product.ProductResult;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.PageResponse;
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
            @ModelAttribute ProductV1Request.Search search) {
        ProductResult.ProductPage productPage = productFacade.search(
                search.toQuery()
        );

        return ApiResponse.success(
                PageResponse.of(
                        productPage.limit(),
                        productPage.offset(),
                        productPage.totalCount(),
                        productPage.products().stream()
                                .map(ProductV1Response.ProductSummary::of)
                                .toList()

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

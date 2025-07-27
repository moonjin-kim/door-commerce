package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductFacade;
import com.loopers.application.product.ProductResult;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Pageable;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductV1Controller implements ProductV1ApiSpec{
    private final ProductFacade productFacade;

    @GetMapping("/{productId}")
    @Override
    public ApiResponse<ProductV1Response.ProductDetail> getBy(@PathVariable(value = "productId")Long brandId) {
        ProductResult.ProductDto product = productFacade.getBy(brandId);

        return ApiResponse.success(
                ProductV1Response.ProductDetail.of(product)
        );
    }

    @Override
    public ApiResponse<PageResponse<ProductV1Response.ProductDetail>> getList(ProductV1Request.Search search, Pageable pageable) {
        return null;
    }
}

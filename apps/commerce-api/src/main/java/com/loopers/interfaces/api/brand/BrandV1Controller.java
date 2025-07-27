package com.loopers.interfaces.api.brand;

import com.loopers.application.brand.BrandFacade;
import com.loopers.application.brand.BrandResult;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/brands")
public class BrandV1Controller implements BrandV1ApiSpec{
    private final BrandFacade brandFacade;

    @GetMapping("/{brandId}")
    @Override
    public ApiResponse<BrandV1ResponseDto.Brand> get(@PathVariable(value = "brandId") Long brandId) {
        BrandResult.BrandDto brandDto = brandFacade.getBrand(brandId);

        return ApiResponse.success(
            BrandV1ResponseDto.Brand.from(brandDto)
        );
    }
}

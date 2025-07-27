package com.loopers.application.brand;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BrandFacade {
    private final BrandService brandService;

    public BrandResult.BrandDto getBrand(Long brandId) {
        Brand brand = brandService.findBy(brandId).orElseThrow(
                () -> new CoreException(ErrorType.NOT_FOUND, "[brandId = " + brandId + "] 존재하지 않는 브랜드입니다.")
        );

        return BrandResult.BrandDto.from(brand);
    }
}

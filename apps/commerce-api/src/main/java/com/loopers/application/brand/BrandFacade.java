package com.loopers.application.brand;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class BrandFacade {
    private final BrandService brandService;

    public BrandResult.BrandDto getBrand(Long brandId) {
        Brand brand = brandService.getBy(brandId).orElseThrow(() -> {
            throw new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 브랜드입니다: " + brandId);
        });

        return BrandResult.BrandDto.from(brand);
    }
}

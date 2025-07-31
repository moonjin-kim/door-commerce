package com.loopers.application.brand;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandInfo;
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
        BrandInfo brand = brandService.findBy(brandId);

        return BrandResult.BrandDto.from(brand);
    }
}

package com.loopers.application.brand;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandInfo;

public class BrandResult {
    public record BrandDto(Long id, String name, String description, String logoUrl) {
        public static BrandDto from(BrandInfo brand) {
            return new BrandDto(
                    brand.id(),
                    brand.name(),
                    brand.description(),
                    brand.logoUrl()
            );
        }
    }
}

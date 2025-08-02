package com.loopers.interfaces.api.brand;

import com.loopers.application.brand.BrandResult;

public class BrandV1Response {
    public record Brand(
            Long brandId,
            String name,
            String description,
            String logoUrl
    ){
        public static Brand from(BrandResult.BrandDto brandDto) {
            return new Brand(
                    brandDto.id(),
                    brandDto.name(),
                    brandDto.description(),
                    brandDto.logoUrl()
            );
        }
    }
}

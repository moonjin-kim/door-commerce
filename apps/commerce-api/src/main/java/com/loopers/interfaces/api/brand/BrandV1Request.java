package com.loopers.interfaces.api.brand;

public class BrandV1Request {
    public record BrandSearchRequest(Long brandId, String sort
    ) {
    }

}

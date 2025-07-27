package com.loopers.interfaces.api.product;

public class ProductV1Request {
    public record Search(
            Long brandId,
            String sort
    ){}
}

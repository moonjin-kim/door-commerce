package com.loopers.domain.product;

import com.loopers.domain.product.vo.ProductStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class ProductView {
    private Long id;
    private String name;
    private Long brandId;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private ProductStatus status;
    private Long likeCount;

    public ProductView(
            Long id,
            String name,
            Long brandId,
            String description,
            String imageUrl,
            BigDecimal price,
            ProductStatus status,
            Long likeCount
    ) {
        this.id = id;
        this.name = name;
        this.brandId = brandId;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.status = status;
        this.likeCount = likeCount;
    }
}

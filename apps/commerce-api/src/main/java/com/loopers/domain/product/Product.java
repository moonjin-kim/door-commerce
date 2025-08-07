package com.loopers.domain.product;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.Money;
import com.loopers.domain.product.vo.ProductStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Product extends BaseEntity {
    @Column(unique = true)
    private String name;
    @Column()
    private Long brandId;
    @Column(unique = true)
    private String description;
    @Column(unique = true)
    private String imageUrl;
    @Embedded
    private Money price;
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    protected Product(String name, Long brandId, String description, String imageUrl, Long price, ProductStatus status) {
        ProductValidator.validateName(name);
        ProductValidator.validateBrandId(brandId);
        ProductValidator.validateDescription(description);
        ProductValidator.validateImageUrl(imageUrl);

        this.name = name;
        this.brandId = brandId;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = new Money(price);

        this.status = status;
    }

    public static Product create(ProductCommand.Create command) {
        return new Product(
                command.name(),
                command.brandId(),
                command.description(),
                command.imageUrl(),
                command.price(),
                ProductStatus.SALE
        );
    }

}

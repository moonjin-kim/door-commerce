package com.loopers.domain.product;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
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
    private Amount price;
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    protected Product(String name, Long brandId, String description, String imageUrl, Long price, ProductStatus status) {
        ProductValidator.validateName(name);
        this.name = name;

        ProductValidator.validateBrandId(brandId);
        this.brandId = brandId;

        ProductValidator.validateDescription(description);
        this.description = description;

        ProductValidator.validateImageUrl(imageUrl);
        this.imageUrl = imageUrl;


        this.price = new Amount(price);

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

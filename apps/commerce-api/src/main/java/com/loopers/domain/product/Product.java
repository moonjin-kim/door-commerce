package com.loopers.domain.product;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
    @Column(unique = true)
    private Long brandId;
    @Column(unique = true)
    private String description;
    @Column(unique = true)
    private String imageUrl;
    @Embedded
    private Amount price;

    protected Product(String name, Long brandId, String description, String imageUrl, Long price) {
        ProductValidator.validateName(name);
        this.name = name;

        ProductValidator.validateBrandId(brandId);
        this.brandId = brandId;

        ProductValidator.validateDescription(description);
        this.description = description;

        ProductValidator.validateImageUrl(imageUrl);
        this.imageUrl = imageUrl;


        this.price = new Amount(price);
    }

    public static Product create(ProductCommand.Create command) {
        return new Product(
                command.name(),
                command.brandId(),
                command.description(),
                command.imageUrl(),
                command.price()
        );
    }

}

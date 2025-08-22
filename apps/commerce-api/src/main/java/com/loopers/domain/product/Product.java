package com.loopers.domain.product;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.Money;
import com.loopers.domain.product.vo.ProductStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Product extends BaseEntity {
    @Column(unique = true)
    private String name;
    @Column()
    private Long brandId;
    @Column()
    private String description;
    @Column(unique = true)
    private String imageUrl;
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "price", nullable = false, precision = 10, scale = 2))
    private Money price;
    @Enumerated(EnumType.STRING)
    private ProductStatus status;
    private Long likeCount;

    protected Product(
            String name,
            Long brandId,
            String description,
            String imageUrl,
            Long price,
            ProductStatus status
    ) {
        ProductValidator.validateName(name);
        ProductValidator.validateBrandId(brandId);
        ProductValidator.validateDescription(description);
        ProductValidator.validateImageUrl(imageUrl);

        this.name = name;
        this.brandId = brandId;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = new Money(new BigDecimal(price));
        this.likeCount = 0L;
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

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

}

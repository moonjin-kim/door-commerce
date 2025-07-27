package com.loopers.domain.brand;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "brand")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class Brand extends BaseEntity {
    @Column(length = 20, unique = true)
    String name;
    @Column(length = 1000)
    String description;
    @Column(length = 200)
    String logoUrl;

    public Brand(String name, String description, String logoUrl) {
        BrandValidator.validateName(name);
        this.name = name;

        BrandValidator.validateDescription(description);
        this.description = description;

        BrandValidator.validateLogoUrl(logoUrl);
        this.logoUrl = logoUrl;
    }

    public static Brand create(BrandCommand.Create command) {
        return new Brand(
                command.name(),
                command.description(),
                command.logoUrl()
        );
    }
}

package com.loopers.domain.brand;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
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
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    BrandStatus status;

    public Brand(String name, String description, String logoUrl, BrandStatus status) {
        BrandValidator.validateName(name);
        this.name = name;

        BrandValidator.validateDescription(description);
        this.description = description;

        BrandValidator.validateLogoUrl(logoUrl);
        this.logoUrl = logoUrl;

        if(status == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Status cannot be null");
        }
        this.status = status;
    }

    public static Brand create(BrandCommand.Create command) {
        return new Brand(
                command.name(),
                command.description(),
                command.logoUrl(),
                BrandStatus.ACTIVE
        );
    }
}

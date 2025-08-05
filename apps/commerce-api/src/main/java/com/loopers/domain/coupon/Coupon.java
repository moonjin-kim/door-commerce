package com.loopers.domain.coupon;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.coupon.policy.DiscountPolicy;
import com.loopers.domain.coupon.policy.FixedAmountDiscountPolicy;
import com.loopers.domain.coupon.policy.PercentDiscountPolicy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "coupon_type")
@NoArgsConstructor
@Entity
@Table(name = "coupon")
public class Coupon extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;

    @Column(nullable = false)
    private BigDecimal discountValue;

    protected Coupon(String name, String description, BigDecimal discountValue, DiscountType discountType) {
        CouponValid.validateName(name);
        CouponValid.validateDescription(description);
        CouponValid.validateDiscountPolicy(discountValue, discountType);

        this.name = name;
        this.description = description;
        this.discountType = discountType;
        this.discountValue = discountValue;
    }

    public static Coupon create(CouponCommand.Create command) {
        return new Coupon(command.name(), command.description(), command.value(), command.type());
    }

    public BigDecimal applyDiscount(BigDecimal price) {
        DiscountPolicy policy = switch (discountType) {
            case DiscountType.PERCENT -> new PercentDiscountPolicy(discountValue);
            case DiscountType.FIXED -> new FixedAmountDiscountPolicy(discountValue);
        };
        return policy.applyDiscount(price);
    }

}

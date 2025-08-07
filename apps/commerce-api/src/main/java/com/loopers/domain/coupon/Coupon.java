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
    private DiscountType type;

    @Column(nullable = false)
    private BigDecimal value;

    protected Coupon(String name, String description, BigDecimal value, DiscountType type) {
        CouponValid.validateName(name);
        CouponValid.validateDescription(description);
        CouponValid.validateDiscountPolicy(value, type);

        this.name = name;
        this.description = description;
        this.type = type;
        this.value = value;
    }

    public static Coupon create(CouponCommand.Create command) {
        return new Coupon(command.name(), command.description(), command.value(), command.type());
    }

}

package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.Money;
import com.loopers.domain.coupon.policy.DiscountPolicy;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // order가 SQL 예약어인 경우가 많아 orders로 지정
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    @Column(nullable = false)
    private Long userId;
    @AttributeOverrides({
            @AttributeOverride(name="value", column = @Column(name="total_amount"))
    })
    private Money totalAmount;
    @Column
    private Long userCouponId;
    @AttributeOverrides({
            @AttributeOverride(name="value", column = @Column(name="coupont_discounted"))
    })
    private Money couponDiscountAmount;
    @AttributeOverrides({
            @AttributeOverride(name="value", column = @Column(name="final_amount"))
    })
    private Money finalAmount;
    @Column(nullable = false)
    LocalDateTime orderDate;
    @Enumerated(EnumType.STRING)
    @Column
    private OrderStatus status;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "order_item", // OrderItem 정보를 저장할 별도 테이블의 이름
            joinColumns = @JoinColumn(name = "order_id") // 이 테이블에서 Order를 참조할 외래 키(FK)
    )
    private List<OrderItem> orderItems = new ArrayList<>();

    private Order(Long userId, List<OrderItem> items, OrderStatus status) {
        if(userId == null || items == null || items.isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST,"주문 정보가 올바르지 않습니다.");
        }
        this.userId = userId;
        this.orderItems = items;
        this.status = status;
        this.totalAmount = new Money(calculateTotalPrice());
        this.couponDiscountAmount = new Money(BigDecimal.ZERO);
        this.finalAmount = this.totalAmount;
        this.orderDate = LocalDateTime.now();
    }

    public static Order create(OrderCommand.Order command) {
        List<OrderItem> orderItems = command.orderItems().stream()
                .map(OrderItem::create)
                .toList();

        return new Order(command.userId(), orderItems, OrderStatus.CONFIRMED);
    }

    private BigDecimal calculateTotalPrice() {
        return orderItems.stream()
                .map(OrderItem::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void checkPermission(Long userId) {
        if(userId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용자 ID가 제공되지 않았습니다.");
        }

        if(!this.userId.equals(userId)) {
            throw new CoreException(ErrorType.FORBIDDEN, "해당 주문에 대한 권한이 없습니다.");
        }
    }

    public void applyCoupon(Long userCouponId, BigDecimal discountAmount) {
        if (userCouponId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "쿠폰 정보가 제공되지 않았습니다.");
        }
        if(discountAmount == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "할인 정책이 제공되지 않았습니다.");
        }

        // 쿠폰 적용 로직
        this.couponDiscountAmount = new Money(discountAmount);
        this.userCouponId = userCouponId;

        calculateFinalAmount();
    }

//    public void applyCoupon(Long userCouponId, DiscountPolicy discountPolicy) {
//        if (userCouponId == null) {
//            throw new CoreException(ErrorType.BAD_REQUEST, "쿠폰 정보가 제공되지 않았습니다.");
//        }
//        if(discountPolicy == null) {
//            throw new CoreException(ErrorType.BAD_REQUEST, "할인 정책이 제공되지 않았습니다.");
//        }
//
//        BigDecimal discount = discountPolicy.calculateDiscount(calculateTotalPrice());
//
//        // 쿠폰 적용 로직
//        this.couponDiscountAmount = new Money(discount);
//        this.userCouponId = userCouponId;
//
//        calculateFinalAmount();
//    }

    public void calculateFinalAmount() {
        if (this.couponDiscountAmount == null) {
            this.finalAmount = this.totalAmount;
        } else {
            this.finalAmount = this.totalAmount.minus(this.couponDiscountAmount.value());
        }
    }
}

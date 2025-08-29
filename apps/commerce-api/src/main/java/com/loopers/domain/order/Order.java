package com.loopers.domain.order;

import com.github.f4b6a3.uuid.UuidCreator;
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
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    @Column(unique = true, nullable = false, length = 36)
    private String orderId;
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
    @Column(length = 20)
    private OrderStatus status;

    private final int PENDING_LIMIT_SECONDS = 5;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OrderItem> orderItems = new ArrayList<>();

    private Order(Long userId, List<OrderItem> items, OrderStatus status) {
        if(userId == null || items == null || items.isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST,"주문 정보가 올바르지 않습니다.");
        }

        items.forEach(this::addOrderItem);
        this.userId = userId;
        this.status = status;
        this.totalAmount = new Money(calculateTotalPrice());
        this.couponDiscountAmount = new Money(BigDecimal.ZERO);
        this.finalAmount = this.totalAmount;
        this.orderDate = LocalDateTime.now();
        this.orderId = generateOrderCode();
    }

    public static Order create(OrderCommand.Order command) {
        List<OrderItem> orderItems = command.orderItems().stream()
                .map(OrderItem::create)
                .toList();

        return new Order(command.userId(), orderItems, OrderStatus.PENDING);
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

    public void addOrderItem(final OrderItem item) {
        orderItems.add(item);
        item.initOrder(this);
    }

    public void calculateFinalAmount() {
        if (this.couponDiscountAmount == null) {
            this.finalAmount = this.totalAmount;
        } else {
            this.finalAmount = this.totalAmount.minus(this.couponDiscountAmount.value());
        }
    }


    private String generateOrderCode() {
        return UuidCreator.getTimeOrdered().toString();
    }


    private BigDecimal calculateTotalPrice() {
        return orderItems.stream()
                .map(OrderItem::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void complete() {
        if (this.status != OrderStatus.PENDING) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문 상태가 PENDING이 아닙니다. 현재 상태: " + this.status);
        }

        this.status = OrderStatus.COMPLETED;
    }

    public void cancel() {
        if (this.status != OrderStatus.PENDING) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문 상태가 PENDING이 아닙니다. 현재 상태: " + this.status);
        }

        this.status = OrderStatus.CANCELLED;
    }

    public boolean isExpire(LocalDateTime currentTime) {
        if (this.status != OrderStatus.PENDING) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문 상태가 PENDING이 아닙니다. 현재 상태: " + this.status);
        }

        return currentTime.isAfter(this.orderDate.plusSeconds(PENDING_LIMIT_SECONDS));
    }
}

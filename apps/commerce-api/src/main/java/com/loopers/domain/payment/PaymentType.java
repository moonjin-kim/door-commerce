package com.loopers.domain.payment;

import lombok.Getter;

@Getter
public enum PaymentType {
    CARD("CARD"),
    POINT("POINT");

    private final String beanName;

    PaymentType(String beanName) {
        this.beanName = beanName;
    }


    static public PaymentType of(String type) {
        for (PaymentType paymentType : PaymentType.values()) {
            if (paymentType.getBeanName().equalsIgnoreCase(type)) {
                return paymentType;
            }
        }
        throw new IllegalArgumentException("Unknown payment status: " + type);
    }

}

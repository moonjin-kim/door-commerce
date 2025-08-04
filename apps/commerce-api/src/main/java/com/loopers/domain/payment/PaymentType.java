package com.loopers.domain.payment;

public enum PaymentType {
    CREDIT_CARD("creditCardPayment"),
    POINT("pointPayment");

    private final String beanName;

    PaymentType(String beanName) {
        this.beanName = beanName;
    }


    static public PaymentType of(String status) {
        for (PaymentType paymentType : PaymentType.values()) {
            if (paymentType.getBeanName().equalsIgnoreCase(status)) {
                return paymentType;
            }
        }
        throw new IllegalArgumentException("Unknown payment status: " + status);
    }

    public String getBeanName() {
        return beanName;
    }
}

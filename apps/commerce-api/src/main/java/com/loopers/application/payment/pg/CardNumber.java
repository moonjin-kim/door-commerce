package com.loopers.application.payment.pg;

public class CardNumber {
    private final String number;

    public CardNumber(String number) {
        if (number == null || number.isEmpty()) {
            throw new IllegalArgumentException("Card number cannot be null or empty");
        }
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "CardNumber{" +
                "number='" + number + '\'' +
                '}';
    }
}

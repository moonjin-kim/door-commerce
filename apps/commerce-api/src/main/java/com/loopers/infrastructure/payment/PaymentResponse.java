package com.loopers.infrastructure.payment;

public class PaymentResponse<T> {
    private Meta meta;
    private T data;

    public Meta getMeta() { return meta; }
    public T getData() { return data; }

    public static class Meta {
        private String result;
        public String getResult() { return result; }
    }
}

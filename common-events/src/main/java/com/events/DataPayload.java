package com.events;

public class DataPayload {
// class for payload business
    private final String orderId;
    private final Long amount;
    private final String currency;

    public DataPayload(String orderId, Long amount, String currency) {
        this.orderId = orderId;
        this.amount = amount;
        this.currency = currency;
    }

    public String getOrderId() {
        return orderId;
    }

    public Long getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }
}

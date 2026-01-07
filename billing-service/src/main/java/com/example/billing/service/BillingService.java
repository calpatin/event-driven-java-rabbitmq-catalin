package com.example.billing.service;

public class BillingService {

    public void charge(String orderId, Long amount) {
//        payment simulation

        if ("FAIL_TEMP".equals(orderId)) {
            throw new RuntimeException("Temporary payment provider error");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }

    }
}

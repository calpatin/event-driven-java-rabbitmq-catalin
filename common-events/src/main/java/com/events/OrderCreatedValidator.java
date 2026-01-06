package com.events;

public class OrderCreatedValidator {

    public static void validateOrder(OrderCreatedEvent event) {
        if (event.getDataPayload().getOrderId() == null) {
            throw new InvalidPayloadException("orderId is null");
        }

        if (event.getDataPayload().getAmount() == null) {
            throw new InvalidPayloadException("amount is null");
        }

        if (event.getDataPayload().getAmount() <= 0) {
            throw new InvalidPayloadException("amount must be positive");
        }
    }
}

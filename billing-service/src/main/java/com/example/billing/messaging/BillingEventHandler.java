package com.example.billing.messaging;

import com.example.billing.service.BillingService;
import com.example.events.InvalidPayloadException;
import com.example.events.OrderCreatedEvent;

public class BillingEventHandler {

//    handle(event):
//  if payload invalid -> InvalidPayloadException
//  if temporary problem -> RetryableException
//  else -> SUCCESS

    private final BillingService billingService;

    public BillingEventHandler(BillingService billingService) {
        this.billingService = billingService;
    }

    public void handle(OrderCreatedEvent event) {
//        payload validation

        if (event == null || event.getDataPayload() == null) {
            throw new InvalidPayloadException("Event or payload is null");
        }

        String orderId = event.getDataPayload().getOrderId();
        Long amount = event.getDataPayload().getAmount();

        if (orderId == null || amount == null) {
            throw new InvalidPayloadException("Missing mandatory fields");
        }

//        business logic
        try {
            billingService.charge(orderId, amount);
        } catch (IllegalArgumentException e) {
//            invalid data -> Dead Letter Queue
            throw new InvalidPayloadException(e.getMessage());
        } catch (Exception e) {
//            temporary error -> retry
            throw new RetryableException("Temporary billing failure", e);
        }
//        success -> ACK
    }
}

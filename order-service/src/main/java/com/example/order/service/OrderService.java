package com.example.order.service;

import com.example.order.messaging.OrderEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderEventPublisher eventPublisher;

    public OrderService(OrderEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void createOrder(String orderId, Long amount, String currency) {
        eventPublisher.publishOrderCreated(orderId, amount, currency);
    }

}

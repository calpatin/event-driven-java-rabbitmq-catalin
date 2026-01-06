package com.example.order.messaging;

import com.events.DataPayload;
import com.events.Metadata;
import com.events.OrderCreatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class OrderEventPublisher {
//    publish OrderCreatedEvent to RabbitMQ

    public static final String EXCHANGE = "order.events";
    public static final String ROUTING_KEY = "order.created";
    private final RabbitTemplate rabbitTemplate;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishOrderCreated(String orderId, Long amount, String currency) {
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(),
                Instant.now(),
                1,
                new DataPayload(orderId, amount, currency),
                new Metadata(
                        UUID.randomUUID().toString(),
                        "order-service"
                )
        );
        rabbitTemplate.convertAndSend(
                EXCHANGE,
                ROUTING_KEY,
                event
        );
    }
}

package com.example.events;

import com.events.DataPayload;
import com.events.Metadata;
import com.events.OrderCreatedEvent;

import java.time.Instant;
import java.util.UUID;

public class TestEventFactory {

    public static OrderCreatedEvent validOrderCreatedEvent() {
        return new OrderCreatedEvent(
                UUID.randomUUID(),
                Instant.now(),
                1,
                new DataPayload("order-123", 100L, "EUR"),
                new Metadata("corr-123", "oder-service")
        );
    }

    public static OrderCreatedEvent invalidOrderCreatedEventWithoutOrderID() {
        return new OrderCreatedEvent(
                UUID.randomUUID(),
                Instant.now(),
                1,
                new DataPayload(null, 100L, "EUR"),
                new Metadata("corr-123", "order-service")
        );
    }
}

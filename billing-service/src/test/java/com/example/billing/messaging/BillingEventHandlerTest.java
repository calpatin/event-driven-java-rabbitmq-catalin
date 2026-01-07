package com.example.billing.messaging;

import com.example.billing.service.BillingService;
import com.example.events.DataPayload;
import com.example.events.InvalidPayloadException;
import com.example.events.OrderCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BillingEventHandlerTest {

    private BillingEventHandler handler;

    @BeforeEach
    void setup() {
        BillingService billingService = new BillingService();
        handler = new BillingEventHandler(billingService);
    }

    //    happy path -> success
    @Test
    void valid_event_is_processed_successfully() {
        OrderCreatedEvent event = validEvent("order-123", 100L);

        assertThatNoException().isThrownBy(() -> handler.handle(event));
    }

    //    invalid payload -> DLQ
    @Test
    void invalid_payload_throws_InvalidPayloadException() {
        OrderCreatedEvent event = validEvent(null, 100L);

        assertThatThrownBy(() -> handler.handle(event))
                .isInstanceOf(InvalidPayloadException.class)
                .hasMessageContaining("Missing mandatory fields");
    }

    //    temporary error ->retry
    @Test
    void temporary_error_throws_RetryableException() {
        OrderCreatedEvent event = validEvent("FAIL_TEMP", 100L);

        assertThatThrownBy(() -> handler.handle(event))
                .isInstanceOf(RetryableException.class)
                .hasMessageContaining("Temporary billing failure");
    }

    private OrderCreatedEvent validEvent(String orderId, Long amount) {
        return new OrderCreatedEvent(
                UUID.randomUUID(),
                Instant.now(),
                1,
                new DataPayload(orderId, amount, "EUR"),
                null
        );
    }
}

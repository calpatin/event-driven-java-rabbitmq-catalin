package com.example.billing.messaging;

import com.example.events.InvalidPayloadException;
import com.example.events.OrderCreatedEvent;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class BillingConsumerTest {

    private BillingEventHandler handler;
    private BillingConsumer consumer;
    private Channel channel;

    private static final long DELIVERY_TAG = 1L;

    @BeforeEach
    void setup() {
        handler = mock(BillingEventHandler.class);
        consumer = new BillingConsumer(handler);
        channel = mock(Channel.class);
    }

    //    success ->ACK
    @Test
    void successful_processing_aknowledges_message() throws Exception {

        OrderCreatedEvent event = dummyEvent();
        // handler.handle(event) does nothing → success

        consumer.onMessage(event, channel, DELIVERY_TAG);

        verify(channel).basicAck(DELIVERY_TAG, false);
        verifyNoMoreInteractions(channel);
    }

    //    retry -> NACK + requeu
    @Test
    void retryable_exception_requeues_message() throws Exception {

        OrderCreatedEvent event = dummyEvent();

        doThrow(new RetryableException("Temporary Failures"))
                .when(handler).handle(event);

        consumer.onMessage(event, channel, DELIVERY_TAG);

        verify(channel).basicNack(DELIVERY_TAG, false, true);
        verifyNoMoreInteractions(channel);
    }

    //    invalid payload -> NACK + No requeue -> DLQ
    @Test
    void invalid_payload_sends_message_to_dlq() throws Exception {

        OrderCreatedEvent event = dummyEvent();

        doThrow(new InvalidPayloadException("Invalid payload"))
                .when(handler).handle(event);

        consumer.onMessage(event, channel, DELIVERY_TAG);

        verify(channel).basicNack(DELIVERY_TAG, false, false);
        verifyNoMoreInteractions(channel);

    }

    private OrderCreatedEvent dummyEvent() {
        return new OrderCreatedEvent(
                UUID.randomUUID(),
                Instant.now(),
                1,
                null,
                null
        );
    }
}

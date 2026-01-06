package com.example.order.producer;

import com.example.events.OrderCreatedEvent;
import com.example.order.messaging.OrderEventPublisher;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.Long;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = OrderEventPublisherTest.TestConfig.class)
class OrderEventPublisherTest {

    @Configuration
    @ComponentScan(basePackageClasses = OrderEventPublisher.class)
    static class TestConfig {
        // scans OrderEventPublisher only
    }

    @MockBean
    RabbitTemplate rabbitTemplate;

    @Autowired
    OrderEventPublisher publisher;

    @Test
    void publishes_OrderCreated_event_with_correct_routing_and_payload() {

        // WHEN
        publisher.publishOrderCreated(
                "order-123",
                100L,
                "EUR"
        );

        // THEN
        ArgumentCaptor<String> exchangeCaptor =
                ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> routingKeyCaptor =
                ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> payloadCaptor =
                ArgumentCaptor.forClass(Object.class);

        verify(rabbitTemplate).convertAndSend(
                exchangeCaptor.capture(),
                routingKeyCaptor.capture(),
                payloadCaptor.capture()
        );

        assertThat(exchangeCaptor.getValue())
                .isEqualTo("order.events");
        assertThat(routingKeyCaptor.getValue())
                .isEqualTo("order.created");

        OrderCreatedEvent event =
                (OrderCreatedEvent) payloadCaptor.getValue();

        assertThat(event).isNotNull();
        assertThat(event.getDataPayload().getOrderId())
                .isEqualTo("order-123");
        assertThat(event.getDataPayload().getAmount())
                .isEqualTo(100L);
        assertThat(event.getMetadata().getSource())
                .isEqualTo("order-service");
    }
}

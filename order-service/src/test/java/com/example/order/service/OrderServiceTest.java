package com.example.order.service;

import com.example.order.messaging.OrderEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = OrderServiceTest.TestConfig.class)
class OrderServiceTest {

    @Configuration
    @ComponentScan(basePackageClasses = OrderService.class)
    static class TestConfig {
        // scans OrderService only
    }

    @MockBean
    OrderEventPublisher publisher;

    @Autowired
    OrderService orderService;

    @Test
    void createOrder_calls_event_publisher() {

        // WHEN
        orderService.createOrder(
                "order-123",
                100L,
                "EUR"
        );

        // THEN
        ArgumentCaptor<String> orderIdCaptor =
                ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> amountCaptor =
                ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> currencyCaptor =
                ArgumentCaptor.forClass(String.class);

        verify(publisher).publishOrderCreated(
                orderIdCaptor.capture(),
                amountCaptor.capture(),
                currencyCaptor.capture()
        );

        assertThat(orderIdCaptor.getValue()).isEqualTo("order-123");
        assertThat(amountCaptor.getValue())
                .isEqualTo(100);
        assertThat(currencyCaptor.getValue()).isEqualTo("EUR");
    }
}

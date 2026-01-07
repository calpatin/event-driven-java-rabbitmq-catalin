package com.example.billing.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String ORDER_EVENTS_EXCHANGE = "order.events";
    public static final String BILLING_QUEUE = "billing.order.created";
    public static final String DLQ = "billing.order.created.dlq";

    @Bean
    public Queue billingQueue() {
        return QueueBuilder.durable(BILLING_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", DLQ)
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ).build();
    }

    @Bean
    public Binding billingBinding() {
        return BindingBuilder
                .bind(billingQueue())
                .to(new TopicExchange(ORDER_EVENTS_EXCHANGE))
                .with("order.created");
    }

}

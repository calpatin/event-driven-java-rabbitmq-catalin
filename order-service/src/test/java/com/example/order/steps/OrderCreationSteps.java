package com.example.order.steps;

import com.events.OrderCreatedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import org.awaitility.Awaitility;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderCreationSteps {

    @LocalServerPort
    int port;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    AmqpAdmin amqpAdmin;

    private static final String EXCHANGE = "order.events";
    private static final String ROUTING_KEY = "order.created";

    private String testQueueName;

    @Given("Order Service is running")
    public void orderServiceIsRunning() {
        RestAssured.port = port;
//     create a temporary queue for testing only
        testQueueName = "test.queue." + System.currentTimeMillis();

        Queue testQueue = QueueBuilder
                .nonDurable(testQueueName)
                .autoDelete()
                .build();

        amqpAdmin.declareQueue(testQueue);

        Binding binding = BindingBuilder
                .bind(testQueue)
                .to(new TopicExchange(EXCHANGE))
                .with(ROUTING_KEY);

        amqpAdmin.declareBinding(binding);
    }

    @When("I create an order")
    public void createOrder() {
        RestAssured
                .given()
                .contentType("application/json")
                .body("""
                        {
                           "orderId": "order-123",
                            "amount": 100,
                            "currency": "EUR"
                        }
                        """)
                .when()
                .post("/orders")
                .then()
                .statusCode(201);
    }

    @Then("An OrderCreated event is published")
    public void orderCreatedIsPublished() {

        OrderCreatedEvent event = Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .until(() -> {
                    Object message = rabbitTemplate.receiveAndConvert(
                            EXCHANGE + "." + ROUTING_KEY
                    );
                    return (OrderCreatedEvent) message;
                }, Objects::nonNull);

        assertThat(event).isNotNull();
        assertThat(event.getDataPayload().getOrderId()).isEqualTo("order-123");
        assertThat(event.getDataPayload().getAmount()).isEqualTo(100);
        assertThat(event.getMetadata().getSource()).isEqualTo("order-service");

    }
}

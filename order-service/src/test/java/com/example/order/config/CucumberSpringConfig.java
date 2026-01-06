package com.example.order.config;

import com.example.order.OrderApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@CucumberContextConfiguration
@SpringBootTest(
        classes = OrderApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(properties = {
        "spring.main.web-application-type=servlet"
})
@Testcontainers
public class CucumberSpringConfig {

    @Container
    static RabbitMQContainer rabbitMQContainer =
            new RabbitMQContainer("rabbitmq:3.12-management");

    static {
        rabbitMQContainer.start();
        System.setProperty("spring.rabbitmq.host", rabbitMQContainer.getHost());
        System.setProperty("spring.rabbitmq.port", rabbitMQContainer.getAmqpPort().toString());

    }
}

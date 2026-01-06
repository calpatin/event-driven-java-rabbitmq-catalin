@producer
Feature: Order creation publishes OrderCreated event

  Scenario: Creating an order publishes OrderCreated event
    Given Order Service is running
    When I create an order
    Then An OrderCreated event is published
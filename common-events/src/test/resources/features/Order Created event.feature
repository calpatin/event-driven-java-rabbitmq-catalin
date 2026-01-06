@contractLevel
Feature: OrderCreated event contract

  Scenario: Valid OrderCreated event
    Given I create a valid OrderCreated event
    When The event is validated
    Then The event is marked as valid

  Scenario: Invalid OrderCreated event with missing orderId
    Given I create an OrderCreated event with missing orderId
    When The event is validated
    Then The event is rejected as invalid



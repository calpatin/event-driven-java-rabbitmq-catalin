package com.example.events;

import com.events.InvalidPayloadException;
import com.events.OrderCreatedEvent;
import com.events.OrderCreatedValidator;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderCreatedEventSteps {

    private OrderCreatedEvent event;
    public Exception exception;

    @Given("I create a valid OrderCreated event")
    public void createValidEvent() {
        event = TestEventFactory.validOrderCreatedEvent();
    }

    @Given("I create an OrderCreated event with missing orderId")
    public void createInvalidEvent(){
        event = TestEventFactory.invalidOrderCreatedEventWithoutOrderID();
    }

    @When("The event is validated")
    public void validateEvent() {
        try {
            OrderCreatedValidator.validateOrder(event);
        }
        catch (Exception e) {
            exception = e;
        }
    }

    @Then("The event is marked as valid")
    public void eventIsValid() {
        assertThat(exception).isNull();
    }

    @Then("The event is rejected as invalid")
    public void eventIsInvalid() {
        assertThat(exception).isInstanceOf(InvalidPayloadException.class);
    }


}

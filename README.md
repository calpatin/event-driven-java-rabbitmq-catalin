# Event-Driven Java with RabbitMQ (Mini Project)

This repository is a **small, educational project** that demonstrates **event-driven communication** using Java and RabbitMQ, with a strong focus on **clean architecture and correct testing strategies**.

The project is intentionally minimal and avoids unnecessary infrastructure in order to clearly explain:
- producer vs consumer responsibilities
- ACK / retry / dead-letter decisions
- how event-driven systems can be tested without Docker or a real broker

---

## Project Structure
.
├── common-events
├── order-service
├── billing-service
└── pom.xml

### Modules

- **common-events**  
  Shared event contracts and contract tests.

- **order-service**  
  Event producer that publishes `OrderCreatedEvent`.

- **billing-service**  
  Event consumer that handles ACK, retry, and dead-letter scenarios.

---

## Architecture Overview

The system follows a classic event-driven model:
Order Service
└── publishes OrderCreatedEvent
↓
RabbitMQ
↓
Billing Service


- Producer and consumer are **loosely coupled**
- Communication is **asynchronous**
- Services do not depend on each other directly

---

## common-events (Event Contracts)

This module contains:
- event models (`DomainEvent`, `OrderCreatedEvent`)
- payload and metadata definitions
- validation logic
- **Cucumber contract tests**

Cucumber is used here to:
- describe event contracts clearly
- validate valid vs invalid payloads
- provide a shared language between services

No messaging or infrastructure logic exists in this module.

---

## order-service (Producer)

The Order Service:
- receives a REST request
- creates an order
- publishes `OrderCreatedEvent`

### Producer Testing Strategy

Producer tests **do not use RabbitMQ or Docker**.

They verify:
- event construction
- exchange and routing key
- interaction between service and publisher

Technologies:
- JUnit 5
- Spring Test (minimal context)
- Mockito

**Key principle:**

> Producer tests validate the *intent to publish*, not message delivery.

---

## billing-service (Consumer)

The Billing Service consumes `OrderCreatedEvent` and decides how to react.

### Consumer Decision Flow
handle(event):
if payload invalid -> DLQ
if temporary failure -> retry
else -> ACK


### Decision Mapping

| Situation | Result |
|--------|--------|
| Valid payload, success | ACK |
| Temporary error | Retry (requeue) |
| Invalid payload | Dead Letter Queue |

### Design Principles

- Business logic is isolated in `BillingEventHandler`
- Messaging logic is handled in `BillingConsumer`
- RabbitMQ topology is defined in a dedicated `config` package
- The consumer translates **exceptions into ACK / NACK decisions**

---

## Consumer Testing Strategy

Consumer tests are written **without RabbitMQ**.

They verify:
- business decisions
- retry vs dead-letter behavior
- interaction with the RabbitMQ `Channel`

**Key principle:**

> Consumer tests verify decisions, not broker behavior.

---

## Why No Docker or Testcontainers?

This project intentionally avoids Docker in tests in order to:
- run in restricted environments
- keep tests fast and deterministic
- focus on design rather than infrastructure

The design remains fully compatible with real RabbitMQ deployments.

---

## Key Takeaways

- Producers and consumers require **different testing strategies**
- Event contracts should be tested separately
- Retry and DLQ are **business decisions**
- Minimal Spring contexts improve test clarity and speed
- Event-driven systems can be tested effectively without Docker

---

## Status

This project is intentionally small and focused.
It is designed as a **learning reference**, not a production-ready system.

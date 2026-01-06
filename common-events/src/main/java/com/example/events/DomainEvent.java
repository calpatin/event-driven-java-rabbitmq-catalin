package com.example.events;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {

    UUID getEventId();

    String getEventType();

    Instant getOccurredAt();

    int getVersion();
}

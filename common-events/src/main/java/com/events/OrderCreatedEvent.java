package com.events;

import java.time.Instant;
import java.util.UUID;

public class OrderCreatedEvent implements DomainEvent {

    private final UUID eventId;
    private final Instant occurredAt;
    private final int version;
    private final DataPayload dataPayload;
    private final Metadata metadata;

    public OrderCreatedEvent(UUID eventId, Instant occurredAt, int version, DataPayload dataPayload, Metadata metadata) {
        this.eventId = eventId;
        this.occurredAt = occurredAt;
        this.version = version;
        this.dataPayload = dataPayload;
        this.metadata = metadata;
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public String getEventType() {
        return "OrderCreated";
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public int getVersion() {
        return version;
    }

    public DataPayload getDataPayload() {
        return dataPayload;
    }

    public Metadata getMetadata() {
        return metadata;
    }
}

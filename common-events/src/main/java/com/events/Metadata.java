package com.events;

public class Metadata {
//class for testing and tracing
    private final String correlationId;

    private final String source;

    public Metadata(String correlationId, String source) {
        this.correlationId = correlationId;
        this.source = source;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getSource() {
        return source;
    }
}

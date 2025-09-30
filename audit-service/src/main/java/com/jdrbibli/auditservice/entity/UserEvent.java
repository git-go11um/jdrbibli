package com.jdrbibli.auditservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "user_events")
public class UserEvent {

    @Id
    private String id;
    private Long userId;
    private String eventType;
    private Instant timestamp;
    private String details;

    public UserEvent() {
    }

    public UserEvent(Long userId, String eventType, Instant timestamp, String details) {
        this.userId = userId;
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.details = details;
    }

    // ---- Getters & Setters ----
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}

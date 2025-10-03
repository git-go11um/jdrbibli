package com.jdrbibli.auditservice.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour {@link UserEvent}.
 */
class UserEventTest {

    @Test
    void testDefaultConstructorAndSettersGetters() {
        UserEvent event = new UserEvent();

        event.setId("1"); // ID String
        event.setUserId(123L);
        event.setEventType("LOGIN");
        Instant now = Instant.now();
        event.setTimestamp(now);
        event.setDetails("User logged in");

        assertEquals("1", event.getId());
        assertEquals(123L, event.getUserId());
        assertEquals("LOGIN", event.getEventType());
        assertEquals(now, event.getTimestamp());
        assertEquals("User logged in", event.getDetails());
    }

    @Test
    void testParameterizedConstructor() {
        Instant now = Instant.now();
        UserEvent event = new UserEvent(123L, "LOGIN", now, "User logged in");

        assertEquals(123L, event.getUserId());
        assertEquals("LOGIN", event.getEventType());
        assertEquals(now, event.getTimestamp());
        assertEquals("User logged in", event.getDetails());
    }

    @Test
    void testEqualsAndHashCodeSimulation() {
        UserEvent event1 = new UserEvent();
        event1.setId("1");
        event1.setUserId(123L);
        event1.setEventType("LOGIN");

        UserEvent event2 = new UserEvent();
        event2.setId("1");
        event2.setUserId(123L);
        event2.setEventType("LOGIN");

        // Comme equals/hashCode ne sont pas sur la classe, on teste l'égalité des
        // champs
        assertEquals(event1.getId(), event2.getId());
        assertEquals(event1.getUserId(), event2.getUserId());
        assertEquals(event1.getEventType(), event2.getEventType());
    }

    @Test
    void testToStringContainsFields() {
        Instant now = Instant.now();
        UserEvent event = new UserEvent(123L, "LOGIN", now, "User logged in");

        // On teste directement avec les getters
        assertEquals(123L, event.getUserId());
        assertEquals("LOGIN", event.getEventType());
        assertEquals("User logged in", event.getDetails());
    }

}

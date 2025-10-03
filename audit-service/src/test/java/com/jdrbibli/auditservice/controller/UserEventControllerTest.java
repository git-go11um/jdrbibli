package com.jdrbibli.auditservice.controller;

import com.jdrbibli.auditservice.entity.UserEvent;
import com.jdrbibli.auditservice.service.UserEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour {@link UserEventController}.
 */
class UserEventControllerTest {

    @Mock
    private UserEventService userEventService;

    @InjectMocks
    private UserEventController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConstructor() {
        UserEventController ctrl = new UserEventController(userEventService);
        assertNotNull(ctrl);
    }

    @Test
    void testLogEvent() {
        UserEvent event = new UserEvent();
        event.setId("1"); // ID String
        event.setUserId(123L);
        event.setEventType("LOGIN");
        event.setDetails("User logged in");
        event.setTimestamp(Instant.now()); // Utiliser Instant

        when(userEventService.logEvent(123L, "LOGIN", "User logged in")).thenReturn(event);

        ResponseEntity<UserEvent> response = controller.logEvent(123L, "LOGIN", "User logged in");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(event, response.getBody());

        verify(userEventService, times(1)).logEvent(123L, "LOGIN", "User logged in");
    }

    @Test
    void testGetAllEvents() {
        UserEvent event1 = new UserEvent();
        event1.setId("1");
        event1.setEventType("LOGIN");

        UserEvent event2 = new UserEvent();
        event2.setId("2");
        event2.setEventType("LOGOUT");

        List<UserEvent> events = Arrays.asList(event1, event2);

        when(userEventService.getAllEvents()).thenReturn(events);

        ResponseEntity<List<UserEvent>> response = controller.getAllEvents();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals("LOGIN", response.getBody().get(0).getEventType());

        verify(userEventService, times(1)).getAllEvents();
    }
}

package com.jdrbibli.auditservice.service;

import com.jdrbibli.auditservice.entity.UserEvent;
import com.jdrbibli.auditservice.repository.UserEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour {@link UserEventService}.
 */
class UserEventServiceTest {

    @Mock
    private UserEventRepository repository;

    private UserEventService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new UserEventService(repository);
    }

    @Test
    void testLogEvent() {
        Long userId = 123L;
        String eventType = "LOGIN";
        String details = "User logged in";

        // On prépare le UserEvent mocké que repository.save() doit retourner
        UserEvent savedEvent = new UserEvent(userId, eventType, Instant.now(), details);
        savedEvent.setId("1");

        when(repository.save(any(UserEvent.class))).thenReturn(savedEvent);

        UserEvent result = service.logEvent(userId, eventType, details);

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals(userId, result.getUserId());
        assertEquals(eventType, result.getEventType());
        assertEquals(details, result.getDetails());
        assertNotNull(result.getTimestamp());

        // Vérifie que save() a été appelé avec le bon UserEvent
        ArgumentCaptor<UserEvent> captor = ArgumentCaptor.forClass(UserEvent.class);
        verify(repository, times(1)).save(captor.capture());
        UserEvent captured = captor.getValue();
        assertEquals(userId, captured.getUserId());
        assertEquals(eventType, captured.getEventType());
        assertEquals(details, captured.getDetails());
        assertNotNull(captured.getTimestamp());
    }

    @Test
    void testGetAllEvents() {
        UserEvent event1 = new UserEvent(123L, "LOGIN", Instant.now(), "User logged in");
        event1.setId("1");
        UserEvent event2 = new UserEvent(124L, "LOGOUT", Instant.now(), "User logged out");
        event2.setId("2");

        List<UserEvent> events = Arrays.asList(event1, event2);
        when(repository.findAll()).thenReturn(events);

        List<UserEvent> result = service.getAllEvents();

        assertEquals(2, result.size());
        assertEquals("LOGIN", result.get(0).getEventType());
        assertEquals("LOGOUT", result.get(1).getEventType());

        verify(repository, times(1)).findAll();
    }
}

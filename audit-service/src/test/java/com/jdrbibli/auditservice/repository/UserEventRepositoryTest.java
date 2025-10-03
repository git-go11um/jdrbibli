package com.jdrbibli.auditservice.repository;

import com.jdrbibli.auditservice.entity.UserEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test unitaire minimal pour {@link UserEventRepository} (mocké).
 */
class UserEventRepositoryTest {

    @Test
    void testSaveAndFindAll() {
        UserEventRepository repository = Mockito.mock(UserEventRepository.class);

        UserEvent event1 = new UserEvent(123L, "LOGIN", Instant.now(), "User logged in");
        event1.setId("1");
        UserEvent event2 = new UserEvent(124L, "LOGOUT", Instant.now(), "User logged out");
        event2.setId("2");

        when(repository.save(event1)).thenReturn(event1);
        when(repository.findAll()).thenReturn(Arrays.asList(event1, event2));

        UserEvent saved = repository.save(event1);
        assertEquals(event1, saved);

        List<UserEvent> allEvents = repository.findAll();
        assertEquals(2, allEvents.size());
        assertEquals("LOGIN", allEvents.get(0).getEventType());
        assertEquals("LOGOUT", allEvents.get(1).getEventType());

        verify(repository, times(1)).save(event1);
        verify(repository, times(1)).findAll();
    }
}

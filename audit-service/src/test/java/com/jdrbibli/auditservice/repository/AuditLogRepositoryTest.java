package com.jdrbibli.auditservice.repository;

import com.jdrbibli.auditservice.entity.AuditLog;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test unitaire minimal pour {@link AuditLogRepository} (mocké).
 */
class AuditLogRepositoryTest {

    @Test
    void testSaveAndFindAll() {
        AuditLogRepository repository = Mockito.mock(AuditLogRepository.class);

        AuditLog log1 = new AuditLog("auth-service", "LOGIN", "User logged in");
        log1.setId("1");
        AuditLog log2 = new AuditLog("user-service", "CREATE", "User created");
        log2.setId("2");

        when(repository.save(log1)).thenReturn(log1);
        when(repository.findAll()).thenReturn(Arrays.asList(log1, log2));

        AuditLog saved = repository.save(log1);
        assertEquals(log1, saved);

        List<AuditLog> allLogs = repository.findAll();
        assertEquals(2, allLogs.size());
        assertEquals("LOGIN", allLogs.get(0).getAction());
        assertEquals("CREATE", allLogs.get(1).getAction());

        verify(repository, times(1)).save(log1);
        verify(repository, times(1)).findAll();
    }
}

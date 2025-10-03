package com.jdrbibli.auditservice.service;

import com.jdrbibli.auditservice.entity.AuditLog;
import com.jdrbibli.auditservice.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour {@link AuditLogService}.
 */
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository repository;

    private AuditLogService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new AuditLogService(repository);
    }

    @Test
    void testSave() {
        AuditLog log = new AuditLog("auth-service", "LOGIN", "User logged in");
        log.setId("1");

        when(repository.save(log)).thenReturn(log);

        AuditLog saved = service.save(log);

        assertEquals(log, saved);

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(repository, times(1)).save(captor.capture());
        assertEquals("LOGIN", captor.getValue().getAction());
    }

    @Test
    void testFindAll() {
        AuditLog log1 = new AuditLog("auth-service", "LOGIN", "User logged in");
        log1.setId("1");
        AuditLog log2 = new AuditLog("user-service", "CREATE", "User created");
        log2.setId("2");

        List<AuditLog> logs = Arrays.asList(log1, log2);
        when(repository.findAll()).thenReturn(logs);

        List<AuditLog> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals("LOGIN", result.get(0).getAction());
        assertEquals("CREATE", result.get(1).getAction());

        verify(repository, times(1)).findAll();
    }
}

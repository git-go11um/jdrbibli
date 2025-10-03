package com.jdrbibli.auditservice.controller;

import com.jdrbibli.auditservice.entity.AuditLog;
import com.jdrbibli.auditservice.service.AuditLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour {@link AuditLogController}.
 */
class AuditLogControllerTest {

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuditLogController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConstructor() {
        AuditLogController ctrl = new AuditLogController(auditLogService);
        assertNotNull(ctrl);
    }

    @Test
    void testCreateLog() {
        AuditLog log = new AuditLog();
        log.setId("1");
        log.setServiceName("auth-service");
        log.setAction("LOGIN");
        log.setDetails("User logged in");
        log.setTimestamp(LocalDateTime.now());

        when(auditLogService.save(any(AuditLog.class))).thenReturn(log);

        ResponseEntity<AuditLog> response = controller.createLog(log);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(log, response.getBody());

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogService, times(1)).save(captor.capture());
        assertEquals("LOGIN", captor.getValue().getAction());
    }

    @Test
    void testGetAllLogs() {
        AuditLog log1 = new AuditLog();
        log1.setId("1");
        log1.setAction("CREATE");

        AuditLog log2 = new AuditLog();
        log2.setId("2");
        log2.setAction("DELETE");

        List<AuditLog> logs = Arrays.asList(log1, log2);

        when(auditLogService.findAll()).thenReturn(logs);

        ResponseEntity<List<AuditLog>> response = controller.getAllLogs();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals("CREATE", response.getBody().get(0).getAction());

        verify(auditLogService, times(1)).findAll();
    }

}

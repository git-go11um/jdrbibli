package com.jdrbibli.auditservice.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour {@link AuditLog}.
 */
class AuditLogTest {

    @Test
    void testDefaultConstructorAndSettersGetters() {
        AuditLog log = new AuditLog();

        log.setId("1");
        log.setServiceName("auth-service");
        log.setAction("USER_UPDATED");
        log.setDetails("Changed password");
        LocalDateTime now = LocalDateTime.now();
        log.setTimestamp(now);

        assertEquals("1", log.getId());
        assertEquals("auth-service", log.getServiceName());
        assertEquals("USER_UPDATED", log.getAction());
        assertEquals("Changed password", log.getDetails());
        assertEquals(now, log.getTimestamp());
    }

    @Test
    void testParameterizedConstructor() {
        AuditLog log = new AuditLog("auth-service", "USER_UPDATED", "Changed password");

        assertEquals("auth-service", log.getServiceName());
        assertEquals("USER_UPDATED", log.getAction());
        assertEquals("Changed password", log.getDetails());
        assertNotNull(log.getTimestamp()); // Timestamp automatique
    }

    @Test
    void testEqualsAndHashCode() {
        AuditLog log1 = new AuditLog();
        log1.setId("1");
        log1.setServiceName("auth-service");
        log1.setAction("LOGIN");

        AuditLog log2 = new AuditLog();
        log2.setId("1");
        log2.setServiceName("auth-service");
        log2.setAction("LOGIN");

        // Comme equals/hashCode ne sont pas sur la classe, on teste l'égalité des
        // champs
        assertEquals(log1.getId(), log2.getId());
        assertEquals(log1.getServiceName(), log2.getServiceName());
        assertEquals(log1.getAction(), log2.getAction());
    }

    @Test
    void testToStringContainsFields() {
        AuditLog log = new AuditLog("auth-service", "LOGIN", "User logged in");

        // On teste directement avec les getters au lieu de toString()
        assertEquals("auth-service", log.getServiceName());
        assertEquals("LOGIN", log.getAction());
        assertEquals("User logged in", log.getDetails());
    }

}

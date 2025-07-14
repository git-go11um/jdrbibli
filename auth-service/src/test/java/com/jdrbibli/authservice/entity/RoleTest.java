package com.jdrbibli.authservice.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class RoleTest {

    @Test
    void testConstructorAndGetters() {
        Role role = new Role("ROLE_ADMIN");

        assertNull(role.getId(), "Id should be null initially");
        assertEquals("ROLE_ADMIN", role.getRoleName());
        assertEquals("ROLE_ADMIN", role.getAuthority());
    }

    @Test
    void testSetters() {
        Role role = new Role();

        role.setId(5L);
        role.setRoleName("ROLE_USER");

        assertEquals(5L, role.getId());
        assertEquals("ROLE_USER", role.getRoleName());
        assertEquals("ROLE_USER", role.getAuthority());
    }
}

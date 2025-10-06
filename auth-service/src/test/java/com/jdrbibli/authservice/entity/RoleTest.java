package com.jdrbibli.authservice.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoleTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        Role role = new Role("ROLE_USER");
        role.setId(1L);

        assertThat(role.getId()).isEqualTo(1L);
        assertThat(role.getRoleName()).isEqualTo("ROLE_USER");
        assertThat(role.getAuthority()).isEqualTo("ROLE_USER"); // Spring Security
    }

    @Test
    void testDefaultConstructorAndSetters() {
        Role role = new Role();
        role.setId(2L);
        role.setRoleName("ROLE_ADMIN");

        assertThat(role.getId()).isEqualTo(2L);
        assertThat(role.getRoleName()).isEqualTo("ROLE_ADMIN");
        assertThat(role.getAuthority()).isEqualTo("ROLE_ADMIN");
    }
}

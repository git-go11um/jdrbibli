package com.jdrbibli.authservice.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import java.util.HashSet;

import org.junit.jupiter.api.Test;

public class UserTest {

    @Test
    void testConstructorAndGetters() {
        Role role = new Role("ROLE_USER");
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User user = new User(1L, "pseudoTest", "email@test.com", "passwordHash", roles, "code123", 123456789L);

        assertEquals(1L, user.getId());
        assertEquals("pseudoTest", user.getPseudo());
        assertEquals("email@test.com", user.getEmail());
        assertEquals("passwordHash", user.getPassword());
        assertEquals(roles, user.getRoles());
        assertEquals("code123", user.getResetPasswordCode());
        assertEquals(123456789L, user.getResetPasswordCodeExpiration());
    }

    @Test
    void testSetters() {
        User user = new User();

        user.setId(2L);
        user.setPseudo("pseudoSetter");
        user.setEmail("setter@test.com");
        user.setPassword("passwordSetter");

        Role role = new Role("ROLE_ADMIN");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        user.setResetPasswordCode("resetCodeSetter");
        user.setResetPasswordCodeExpiration(987654321L);

        assertEquals(2L, user.getId());
        assertEquals("pseudoSetter", user.getPseudo());
        assertEquals("setter@test.com", user.getEmail());
        assertEquals("passwordSetter", user.getPassword());
        assertEquals(roles, user.getRoles());
        assertEquals("resetCodeSetter", user.getResetPasswordCode());
        assertEquals(987654321L, user.getResetPasswordCodeExpiration());
    }

    @Test
    void testUserDetailsMethods() {
        Role role1 = new Role("ROLE_USER");
        Role role2 = new Role("ROLE_ADMIN");
        Set<Role> roles = new HashSet<>();
        roles.add(role1);
        roles.add(role2);

        User user = new User(3L, "pseudoDetails", "user@test.com", "pass", roles, null, null);

        assertEquals(roles, user.getAuthorities());
        assertEquals("pseudoDetails", user.getUsername());
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }

    @Test
    void testBuilder() {
        Role role = new Role("ROLE_BUILDER");
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User user = User.builder()
                .id(10L)
                .pseudo("builderPseudo")
                .email("builder@example.com")
                .password("builderPass")
                .roles(roles)
                .resetPasswordCode("resetBuilder")
                .resetPasswordCodeExpiration(123L)
                .build();

        assertEquals(10L, user.getId());
        assertEquals("builderPseudo", user.getPseudo());
        assertEquals("builder@example.com", user.getEmail());
        assertEquals("builderPass", user.getPassword());
        assertEquals(roles, user.getRoles());
        assertEquals("resetBuilder", user.getResetPasswordCode());
        assertEquals(123L, user.getResetPasswordCodeExpiration());
    }
}

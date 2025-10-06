package com.jdrbibli.ouvrage_service.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsServiceTest {

    private final CustomUserDetailsService service = new CustomUserDetailsService();

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUsernameIsAdmin() {
        UserDetails userDetails = service.loadUserByUsername("admin");

        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
        assertEquals("{noop}password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUsernameIsNotAdmin() {
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("unknown"));
    }
}

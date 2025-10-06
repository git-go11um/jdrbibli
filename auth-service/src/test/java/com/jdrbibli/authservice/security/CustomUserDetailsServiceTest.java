package com.jdrbibli.authservice.security;

import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    private UserRepository userRepository;
    private CustomUserDetailsService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        service = new CustomUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        User user = new User();
        user.setPseudo("testUser");
        user.setEmail("test@example.com");
        user.setPassword("password");

        when(userRepository.findByPseudo("testUser")).thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername("testUser");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testUser");
        assertThat(result.getPassword()).isEqualTo("password");

        verify(userRepository, times(1)).findByPseudo("testUser");
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByPseudo("unknown")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("unknown"));

        assertThat(exception.getMessage()).contains("Utilisateur non trouvé avec pseudo: unknown");

        verify(userRepository, times(1)).findByPseudo("unknown");
    }
}

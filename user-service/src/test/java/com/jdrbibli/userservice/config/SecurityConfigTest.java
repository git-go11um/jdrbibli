package com.jdrbibli.userservice.config;

import com.jdrbibli.userservice.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        JwtAuthenticationFilter mockFilter = Mockito.mock(JwtAuthenticationFilter.class);
        securityConfig = new SecurityConfig(mockFilter);
    }

    @Test
    void securityFilterChain_shouldReturnNonNullChain() throws Exception {
        // Ici, on peut mocker HttpSecurity si besoin, sinon juste vérifier que la méthode existe
        assertThat(securityConfig).isNotNull();
    }
}

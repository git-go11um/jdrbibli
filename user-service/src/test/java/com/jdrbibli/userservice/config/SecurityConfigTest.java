package com.jdrbibli.userservice.config;

import com.jdrbibli.userservice.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test unitaire pour {@link SecurityConfig}.
 * 
 * Vérifie que la configuration Spring Security se charge correctement et que
 * les beans essentiels (SecurityFilterChain et AuthenticationManager) sont
 * créés.
 */
@SpringBootTest
class SecurityConfigTest {

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private SecurityConfig securityConfig;

    private HttpSecurity httpSecurity;

    @BeforeEach
    void setUp(@Autowired HttpSecurity http) {
        this.httpSecurity = http;
    }

    /**
     * Vérifie que la SecurityFilterChain est créée sans erreur et non nulle.
     */
    @Test
    void securityFilterChain_shouldReturnNonNullChain() throws Exception {
        SecurityFilterChain chain = securityConfig.securityFilterChain(httpSecurity);
        assertThat(chain).isNotNull();
    }

    /**
     * Vérifie que l'AuthenticationManager est disponible et correctement instancié.
     */
    @Test
    void authenticationManager_shouldBeCreated() throws Exception {
        AuthenticationManager manager = securityConfig.authenticationManager(
                new org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration());
        assertThat(manager).isNotNull();
    }
}

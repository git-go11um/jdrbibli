package com.jdrbibli.ouvrage_service.config;

import com.jdrbibli.ouvrage_service.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SecurityConfigTest {

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private SecurityFilterChain securityFilterChain; // injecté par Spring Boot

    @Autowired
    private AuthenticationManager authenticationManager; // injecté par Spring Boot

    @Autowired
    private PasswordEncoder passwordEncoder; // injecté par Spring Boot

    @Autowired
    private CorsFilter corsFilter; // injecté par Spring Boot

    @Test
    void beans_shouldBeCreated() {
        assertThat(securityConfig).isNotNull();
        assertThat(securityFilterChain).isNotNull();
        assertThat(authenticationManager).isNotNull();
        assertThat(passwordEncoder).isNotNull();
        assertThat(corsFilter).isNotNull();
    }
}

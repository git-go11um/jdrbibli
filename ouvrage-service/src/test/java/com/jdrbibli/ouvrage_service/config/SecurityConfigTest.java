package com.jdrbibli.ouvrage_service.config;

import com.jdrbibli.ouvrage_service.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

    private SecurityConfig securityConfig;

/*     @BeforeEach
    void setUp() {
        JwtTokenProvider mockJwtProvider = Mockito.mock(JwtTokenProvider.class);
        securityConfig = new SecurityConfig(mockJwtProvider); // constructeur de test
    } */

    @Test
    void securityConfig_shouldBeCreated() {
        assertThat(securityConfig).isNotNull();
    }
}

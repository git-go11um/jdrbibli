package com.jdrbibli.authservice.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        jwtService.setJwtSecret("ma-cle-secrete-assez-longue-pour-hmacsha256");
        jwtService.setJwtExpirationMs(3600000L);
    }

    @Test
    void testGenerateAndExtractPseudo() {
        String pseudo = "testUser";
        String token = jwtService.generateToken(pseudo);

        String extractedPseudo = jwtService.extractPseudo(token);

        assertThat(extractedPseudo).isEqualTo(pseudo);
    }
}

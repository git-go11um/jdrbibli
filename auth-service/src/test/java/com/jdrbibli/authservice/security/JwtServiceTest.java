package com.jdrbibli.authservice.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;
    private String secret = "dGhpc2lzYXZlcnlzdHJvbmdzZWNyZXRrZXlmb3J0ZXN0aW5nMTIz";
    private long expirationMs = 1000 * 60 * 60; // 1 heure

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        jwtService.setJwtSecret(secret);
        jwtService.setJwtExpirationMs(expirationMs);
    }

    @Test
    void generateToken_withExtraClaims_shouldReturnValidToken() {
        String pseudo = "userTest";
        Map<String, Object> claims = Map.of("role", "ROLE_USER");

        String token = jwtService.generateToken(claims, pseudo);
        assertThat(token).isNotNull();

        String extractedPseudo = jwtService.extractPseudo(token);
        assertThat(extractedPseudo).isEqualTo(pseudo);
    }

    @Test
    void generateToken_withPseudoAndId_shouldReturnTokenContainingId() {
        String pseudo = "userTest";
        Long userId = 42L;

        String token = jwtService.generateToken(pseudo, userId);
        assertThat(token).isNotNull();

        // Extraction du pseudo
        String extractedPseudo = jwtService.extractPseudo(token);
        assertThat(extractedPseudo).isEqualTo(pseudo);

        // Extraction de l'id depuis les claims
        Number extractedId = jwtService.extractClaim(token, claims -> (Number) claims.get("id"));
        assertThat(extractedId.longValue()).isEqualTo(userId);
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {
        String pseudo = "userValid";
        String token = jwtService.generateToken(pseudo, 1L);

        boolean valid = jwtService.isTokenValid(token, pseudo);
        assertThat(valid).isTrue();
    }

    @Test
    void isTokenValid_shouldReturnFalseForWrongPseudo() {
        String pseudo = "userValid";
        String token = jwtService.generateToken(pseudo, 1L);

        boolean valid = jwtService.isTokenValid(token, "wrongPseudo");
        assertThat(valid).isFalse();
    }

    @Test
    void isTokenExpired_shouldReturnTrueForExpiredToken() throws InterruptedException {
        jwtService.setJwtExpirationMs(5); // expire très vite
        String pseudo = "userExpired";
        String token = jwtService.generateToken(pseudo, 1L);

        // Attendre largement plus longtemps que l’expiration
        Thread.sleep(50);

        boolean valid;
        try {
            valid = jwtService.isTokenValid(token, pseudo);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            valid = false; // attendu : token expiré
        }

        assertThat(valid).isFalse();
    }

    @Test
    void extractExpiration_shouldReturnDateAfterIssuedAt() {
        String pseudo = "userTest";
        String token = jwtService.generateToken(pseudo, 1L);

        Date expiration = jwtService.extractExpiration(token);
        Date issuedAt = jwtService.extractClaim(token, claims -> claims.getIssuedAt());

        // Conversion en Instant pour lever l'ambiguïté
        assertThat(expiration.toInstant()).isAfterOrEqualTo(issuedAt.toInstant());
    }
}

package com.jdrbibli.userservice.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private Key secretKey;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        // Génère une clé HS256
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

        // Encode en Base64 pour l’injecter dans le service
        String base64Key = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        jwtService.setJwtSecret(base64Key);

        // Expiration assez longue pour les tests
        jwtService.setJwtExpirationMs(10_000); // 10 secondes
    }

    @Test
    void generateToken_shouldContainPseudo() {
        String pseudo = "user1";
        String token = jwtService.generateToken(pseudo);

        assertNotNull(token);
        String extracted = jwtService.extractPseudo(token);
        assertEquals(pseudo, extracted);
    }

    @Test
    void generateToken_withExtraClaims_shouldContainClaims() {
        String pseudo = "user2";
        Map<String, Object> claims = Map.of("role", "ADMIN");
        String token = jwtService.generateToken(claims, pseudo);

        assertNotNull(token);
        assertEquals("ADMIN", jwtService.extractClaim(token, c -> c.get("role", String.class)));
        assertEquals(pseudo, jwtService.extractPseudo(token));
    }

    @Test
    void extractExpiration_shouldReturnCorrectDate() {
        String token = jwtService.generateToken("user3");
        Date expiration = jwtService.extractExpiration(token);
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void isTokenValid_shouldReturnTrueBeforeExpiration() {
        String pseudo = "user4";
        String token = jwtService.generateToken(pseudo);

        boolean valid = jwtService.isTokenValid(token, pseudo);
        assertTrue(valid, "Le token doit être valide avant expiration");
    }

    @Test
    void isTokenValid_shouldReturnFalseAfterExpiration() {
        String pseudo = "user5";

        // Token expiré
        String expiredToken = Jwts.builder()
                .setSubject(pseudo)
                .setIssuedAt(new Date(System.currentTimeMillis() - 2000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(secretKey)
                .compact();

        boolean valid = jwtService.isTokenValid(expiredToken, pseudo);
        assertFalse(valid, "Le token doit être invalide après expiration");
    }
}

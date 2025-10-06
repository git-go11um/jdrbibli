package com.jdrbibli.userservice.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import com.jdrbibli.userservice.utils.JwtUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private static final String SECRET = "Z2dKbnRHY0NvcnNDWUR0bFhHZ3R3NzIzbVRZT3o0Sk5GZjBWT2tVdzBrckxKVXphMnJKZThCb21BZzk1OU9sWkQ=";
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        // Création d’un token de test
        String username = "testuser";
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();

        // Test de l’extraction
        String extracted = JwtUtils.extractUsername(token);
        assertEquals(username, extracted);
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        String token = Jwts.builder()
                .setSubject("user1")
                .setIssuedAt(new Date())
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();

        assertTrue(JwtUtils.validateToken(token));
    }

    @Test
    void validateToken_shouldReturnFalseForInvalidToken() {
        // Token corrompu
        String invalidToken = "invalid.token.value";
        assertFalse(JwtUtils.validateToken(invalidToken));
    }

    @Test
    void extractUsername_shouldThrowExceptionForInvalidToken() {
        String invalidToken = "invalid.token.value";
        assertThrows(Exception.class, () -> JwtUtils.extractUsername(invalidToken));
    }
}

package com.jdrbibli.gateway.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import reactor.test.StepVerifier;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

class JwtAuthenticationManagerTest {

    private final String secret = "Zm9vYmFyYmF6cXV4MTIzNDU2Nzg5MGFiY2RlZmdoaWprbG1ub3BxcnN0"; // Base64 clé de test
    private final JwtAuthenticationManager manager = new JwtAuthenticationManager(secret);

    @Test
    void authenticateValidToken() {
        // Crée un JWT valide
        String token = Jwts.builder()
                .setSubject("testuser")
                .setExpiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();

        Authentication auth = new UsernamePasswordAuthenticationToken(token, token);

        StepVerifier.create(manager.authenticate(auth))
                .expectNextMatches(a -> a.getName().equals("testuser"))
                .verifyComplete();
    }

    @Test
    void authenticateInvalidToken() {
        Authentication auth = new UsernamePasswordAuthenticationToken("invalidtoken", "invalidtoken");

        StepVerifier.create(manager.authenticate(auth))
                .verifyComplete(); // Aucun élément attendu
    }
}

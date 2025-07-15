package com.jdrbibli.authservice.security;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Key;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    public void setUp() {
        jwtTokenProvider = new JwtTokenProvider();

        // Générer une clé correcte pour HS512
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        // La passer sous forme de chaîne (attention : dans la vraie vie il vaut mieux
        // passer Key directement)
        String secretString = java.util.Base64.getEncoder().encodeToString(key.getEncoded());
        jwtTokenProvider.setSecretKey(secretString);
    }

    @Test
    public void testCreateAndParseToken() {
        String username = "testUser";
        String token = jwtTokenProvider.createToken(username);

        String extractedUsername = jwtTokenProvider.getUsernameFromToken(token);

        assertThat(extractedUsername).isEqualTo(username);
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    }
}

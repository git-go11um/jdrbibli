package com.jdrbibli.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.Collections;

/**
 * Gestionnaire d'authentification réactif pour valider les tokens JWT.
 */
@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final Key key;

    /**
     * Injecte la clé secrète depuis application.yml
     */
    public JwtAuthenticationManager(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        System.out.println("🔑 [Gateway] JwtAuthenticationManager initialisé avec clé Base64 décodée");
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(authToken)
                    .getBody();

            String username = claims.getSubject();
            System.out.println("[Gateway] ✅ JWT valide pour user: " + username);

            if (username != null) {
                return Mono.just(new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList()));
            }
        } catch (Exception e) {
            System.out.println("[Gateway] ❌ JWT invalide: " + e.getMessage());
            return Mono.empty();
        }
        return Mono.empty();
    }
}

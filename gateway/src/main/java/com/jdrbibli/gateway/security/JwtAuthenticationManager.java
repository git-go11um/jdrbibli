package com.jdrbibli.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;

/**
 * Gestionnaire d'authentification réactif pour valider les tokens JWT.
 * <p>
 * Implémente {@link ReactiveAuthenticationManager} pour Spring Security WebFlux.
 * Cette classe décode et valide les JWT afin d'extraire le nom d'utilisateur
 * et créer un objet {@link Authentication}.
 * <p>
 * ⚠️ Actuellement, le gateway ne l'utilise plus car la validation JWT est désactivée.
 */
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final Key key;

    /**
     * Constructeur qui initialise la clé de signature à partir d'une clé secrète encodée en base64.
     *
     * @param secretKey clé secrète utilisée pour signer et valider les JWT.
     */
    public JwtAuthenticationManager(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Tente d'authentifier un {@link Authentication} à partir d'un JWT.
     *
     * @param authentication objet contenant le JWT dans {@code credentials}.
     * @return un {@link Mono} contenant l'authentification validée, ou vide si le JWT est invalide.
     */
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
            if (username != null) {
                return Mono.just(
                        new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList()));
            }
        } catch (Exception e) {
            return Mono.empty();
        }
        return Mono.empty();
    }
}

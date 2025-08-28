package com.jdrbibli.gateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final Key key;

    // Liste des endpoints publics qui ne nécessitent pas de JWT
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/password-reset/request",
            "/api/auth/password-reset/verify-code",
            "/api/auth/password-reset/confirm",
            "/api/auth/validate-reset-code",
            "/api/auth/password-reset/change",
            "/api/auth/reset-password");

    public JwtAuthenticationFilter(@Value("${jwt.secret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        HttpMethod method = exchange.getRequest().getMethod();

        // 🔹 Afficher tous les headers pour debug
        exchange.getRequest().getHeaders().forEach((name, values) -> {
            values.forEach(value -> System.out.println(name + " = " + value));
        });

        // 1️⃣ Laisser passer OPTIONS et endpoints publics
        if (method == HttpMethod.OPTIONS || PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            log.debug(">>> Endpoint public ou requête OPTIONS, pas de vérification JWT pour {}", path);
            return chain.filter(exchange);
        }

        // 2️⃣ Vérifier JWT pour les autres endpoints
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Aucun en-tête Authorization trouvé ou malformé pour {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            log.info("JWT valide pour l'utilisateur : {}", username);

            exchange = exchange.mutate()
                    .request(r -> r.headers(headers -> headers.set("Authorization", "Bearer " + token)))
                    .build();

        } catch (Exception e) {
            log.error("Token JWT invalide pour {}", path, e);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }
}

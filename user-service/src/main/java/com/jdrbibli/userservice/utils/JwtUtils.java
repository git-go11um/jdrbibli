package com.jdrbibli.userservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;

/**
 * Utilitaire pour la manipulation et la validation des JSON Web Tokens (JWT).
 * 
 * Fournit des méthodes pour extraire le nom d'utilisateur d'un token JWT
 * et pour vérifier sa validité.
 * 
 */
public class JwtUtils {

    private static final String SECRET = "Z2dKbnRHY0NvcnNDWUR0bFhHZ3R3NzIzbVRZT3o0Sk5GZjBWT2tVdzBrckxKVXphMnJKZThCb21BZzk1OU9sWkQ=";
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    /**
     * Extrait le nom d'utilisateur (subject) depuis un token JWT.
     *
     * @param token le token JWT
     * @return le nom d'utilisateur contenu dans le token
     */
    public static String extractUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * Valide un token JWT.
     *
     * @param token le token JWT à valider
     * @return true si le token est valide, false sinon
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

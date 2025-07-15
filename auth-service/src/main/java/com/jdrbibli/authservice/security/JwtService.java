package com.jdrbibli.authservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    // 🔑 Générer la clé à partir du secret
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // Ajoute ces setters pour pouvoir configurer dans les tests
    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public void setJwtExpirationMs(long jwtExpirationMs) {
        this.jwtExpirationMs = jwtExpirationMs;
    }

    /**
     * Générer un token avec claims custom optionnels
     * 
     * @param extraClaims : map de claims supplémentaires
     * @param pseudo      : pseudo de l'utilisateur (subject)
     */
    public String generateToken(Map<String, Object> extraClaims, String pseudo) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(pseudo)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Générer un token simple sans claims custom
     */
    public String generateToken(String pseudo) {
        return generateToken(Map.of(), pseudo);
    }

    /**
     * Extraire un claim particulier du token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extraire le pseudo (subject)
     */
    public String extractPseudo(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Vérifier si le token est encore valide
     */
    public boolean isTokenValid(String token, String pseudo) {
        final String extractedPseudo = extractPseudo(token);
        return (extractedPseudo.equals(pseudo)) && !isTokenExpired(token);
    }

    /**
     * Vérifier si le token est expiré
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extraire la date d'expiration
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extraire tous les claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}

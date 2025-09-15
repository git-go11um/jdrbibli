package com.jdrbibli.authservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

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

    // 🔑 Générer la clé à partir du secret (Base64)
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // Setter utiles pour les tests
    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public void setJwtExpirationMs(long jwtExpirationMs) {
        this.jwtExpirationMs = jwtExpirationMs;
    }

    /**
     * Générer un token avec claims custom optionnels
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
     * Générer un token simple avec l'ID de l'utilisateur inclus dans les claims
     */
    public String generateToken(String pseudo, Long userId) {
        Map<String, Object> claims = Map.of(
                "id", userId // ✅ ID inclus automatiquement
        );
        return generateToken(claims, pseudo);
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

    // 1️⃣ Getter pour récupérer le secret JWT (chaîne Base64) pour debug
    public String getJwtSecret() {
        return this.jwtSecret;
    }

    // 2️⃣ Getter pour récupérer la clé de signature (Key) pour debug
    public Key getSigningKeyForDebug() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    @PostConstruct
    public void init() {
        System.out.println("🔑 JwtService (auth-service) initialisé avec secret=" + getJwtSecret());
        System.out.println("Clé de signature (auth-service) : " + getSigningKeyForDebug());
    }

}

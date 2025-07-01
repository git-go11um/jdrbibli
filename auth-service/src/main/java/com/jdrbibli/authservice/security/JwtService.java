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

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // Générer un token simple avec claims custom optionnels
    public String generateToken(Map<String, Object> extraClaims, String pseudo) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(pseudo) // on met le pseudo comme subject
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Générer un token sans claims custom
    public String generateToken(String username) {
        return generateToken(Map.of(), username);
    }

    // Extraire un claim générique
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extraire le pseudo (subject)
    public String extractPseudo(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Vérifier si le token est valide (subject correspond et pas expiré)
    public boolean isTokenValid(String token, String pseudo) {
        final String extractedPseudo = extractPseudo(token);
        return (extractedPseudo.equals(pseudo)) && !isTokenExpired(token);
    }

    // Vérifier expiration
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extraire la date d’expiration
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Parser le token et obtenir tous les claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

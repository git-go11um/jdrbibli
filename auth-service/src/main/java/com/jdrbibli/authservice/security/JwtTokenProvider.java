package com.jdrbibli.authservice.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey; // Clé secrète pour signer les JWT

    @Value("${jwt.expiration}")
    private long validityInMilliseconds = 3600000; // 1 heure (en millisecondes)

    // Méthode pour créer un token JWT
    public String createToken(String username) {
        return Jwts.builder()
                .setSubject(username) // Utilisateur (email ou pseudo)
                .setIssuedAt(new Date()) // Date de création du token
                .setExpiration(new Date(System.currentTimeMillis() + validityInMilliseconds)) // Durée d'expiration
                .signWith(SignatureAlgorithm.HS512, secretKey) // Signature avec la clé secrète
                .compact();
    }

    // Méthode pour extraire le nom d'utilisateur (email) à partir du token
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Méthode pour vérifier si le token est expiré
    public boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }

    // Méthode pour valider un token
    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }
}

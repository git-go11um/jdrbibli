package com.jdrbibli.authservice.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Composant simple pour gérer les JWT dans l'application.
 * Fournit des méthodes pour :
 * - créer un token
 * - extraire le pseudo (username)
 * - vérifier l'expiration
 */
@Component
public class JwtTokenProvider {

    // Secret pour signer le token (défini dans application.properties)
    @Value("${jwt.secret}")
    private String secretKey;

    // Durée de vie du token en millisecondes (par défaut 1 heure)
    @Value("${jwt.expiration}")
    private long validityInMilliseconds = 3600000;

    // Setters pour tests ou reconfiguration dynamique
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setValidityInMilliseconds(long validityInMilliseconds) {
        this.validityInMilliseconds = validityInMilliseconds;
    }

    /**
     * Crée un token JWT avec le pseudo comme sujet.
     *
     * @param username pseudo de l'utilisateur
     * @return token JWT signé
     */
    public String createToken(String username) {
        return Jwts.builder()
                .setSubject(username)                   // Sujet = pseudo
                .setIssuedAt(new Date())                // Date d'émission
                .setExpiration(new Date(System.currentTimeMillis() + validityInMilliseconds)) // Expiration
                .signWith(SignatureAlgorithm.HS512, secretKey) // Signature avec HS512
                .compact();
    }

    /**
     * Récupère le pseudo (username) depuis le token.
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)              // Vérification de la signature
                .parseClaimsJws(token)                 // Parse le token
                .getBody()                             // Récupère le payload
                .getSubject();                         // Renvoie le pseudo
    }

    /**
     * Vérifie si le token est expiré.
     */
    public boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());          // true si le token est expiré
    }

    /**
     * Valide le token : il est valide s'il n'est pas expiré.
     */
    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }
}

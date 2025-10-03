package com.jdrbibli.ouvrage_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Fournisseur de tokens JWT pour l'authentification.
 * <p>
 * Cette classe permet de générer, valider et lire les informations contenues
 * dans un token JWT. Les tokens expirent après une durée définie (24 heures).
 */
@Component
public class JwtTokenProvider {

    /** Clé secrète utilisée pour signer le token JWT. */
    private static final String JWT_SECRET = "secretkey";

    /** Durée de validité du token en millisecondes (24 heures). */
    private static final long JWT_EXPIRATION = 86400000;

    /**
     * Génère un token JWT pour un utilisateur donné.
     *
     * @param username le nom d'utilisateur pour lequel générer le token
     * @return le token JWT signé
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }

    /**
     * Récupère les claims (informations) contenus dans un token JWT.
     *
     * @param token le token JWT
     * @return les claims extraits du token
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Vérifie si un token JWT est valide.
     * <p>
     * Le token est considéré valide s'il peut être parsé et si sa date
     * d'expiration n'est pas dépassée.
     *
     * @param token le token JWT
     * @return true si le token est valide, false sinon
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}

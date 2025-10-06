package com.jdrbibli.userservice.security;

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

/**
 * Service de gestion des JSON Web Tokens (JWT).
 * 
 * 
 * Ce service permet de générer des tokens JWT, d'extraire des informations
 * depuis un token, et de valider un token pour un utilisateur donné.
 * 
 * 
 * 
 * Les tokens sont signés avec un secret HMAC et ont une durée de vie configurable.
 * 
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    /**
     * Retourne la durée d'expiration des tokens JWT en millisecondes.
     * 
     * @return durée d'expiration en ms
     */
    public long getJwtExpirationMs() {
        return jwtExpirationMs;
    }

    /**
     * Définit le secret utilisé pour signer les tokens.
     *
     * @param jwtSecret secret JWT encodé en Base64
     */
    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    /**
     * Définit la durée d'expiration des tokens JWT.
     *
     * @param jwtExpirationMs durée en millisecondes
     */
    public void setJwtExpirationMs(long jwtExpirationMs) {
        this.jwtExpirationMs = jwtExpirationMs;
    }

    /**
     * Génère un token JWT pour un pseudo donné et avec des claims supplémentaires.
     *
     * @param extraClaims claims additionnels à inclure dans le token
     * @param pseudo      pseudo de l'utilisateur
     * @return token JWT signé
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
     * Génère un token JWT pour un pseudo donné sans claims supplémentaires.
     *
     * @param pseudo pseudo de l'utilisateur
     * @return token JWT signé
     */
    public String generateToken(String pseudo) {
        return generateToken(Map.of(), pseudo);
    }

    /**
     * Extrait une information spécifique (claim) depuis un token JWT.
     *
     * @param <T>            type de la valeur extraite
     * @param token          le token JWT
     * @param claimsResolver fonction permettant d'extraire le claim
     * @return la valeur extraite
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrait le pseudo de l'utilisateur depuis le token JWT.
     *
     * @param token le token JWT
     * @return le pseudo de l'utilisateur
     */
    public String extractPseudo(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Vérifie si un token JWT est valide pour un pseudo donné.
     *
     * @param token  token JWT
     * @param pseudo pseudo de l'utilisateur
     * @return true si le token est valide et non expiré
     */
    public boolean isTokenValid(String token, String pseudo) {
        try {
            final String extractedPseudo = extractPseudo(token);
            return (extractedPseudo.equals(pseudo)) && !isTokenExpired(token);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return false; // token expiré => invalide
        }
    }
    

    /**
     * Vérifie si le token JWT est expiré.
     *
     * @param token token JWT
     * @return true si le token est expiré
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrait la date d'expiration d'un token JWT.
     *
     * @param token token JWT
     * @return date d'expiration
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrait tous les claims depuis un token JWT.
     *
     * @param token token JWT
     * @return objet Claims contenant toutes les informations
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Retourne le secret JWT actuel.
     *
     * @return le secret JWT encodé en Base64
     */
    public String getJwtSecret() {
        return this.jwtSecret;
    }

    /**
     * Retourne la clé de signature HMAC dérivée du secret JWT.
     * Utile pour le debug.
     *
     * @return clé de signature
     */
    public Key getSigningKeyForDebug() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * Initialisation du service, affiche le secret et l'expiration en console.
     */
    @PostConstruct
    public void init() {
        System.out.println("🔑 JwtService initialisé avec secret=" + getJwtSecret());
        System.out.println("⏱️ Expiration configurée=" + getJwtExpirationMs() + " ms");
    }

    /**
     * Retourne la clé de signature pour la génération et vérification des tokens JWT.
     *
     * @return clé HMAC
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}

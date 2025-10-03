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

/**
 * Service pour gérer la création, l'extraction et la validation des JWT.
 * 
 * Fonctionnalités principales :
 * - Génération de token avec des claims personnalisés
 * - Extraction des informations du token (pseudo, expiration, etc.)
 * - Validation du token (correspondance pseudo + non-expiré)
 */
@Service
public class JwtService {

    // Secret JWT encodé en Base64 (défini dans application.properties)
    @Value("${jwt.secret}")
    private String jwtSecret;

    // Durée de vie du token en millisecondes
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    /**
     * Génère la clé de signature à partir du secret JWT.
     * La clé est utilisée pour signer et valider les JWT.
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // Setters pour tests ou reconfiguration dynamique
    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public void setJwtExpirationMs(long jwtExpirationMs) {
        this.jwtExpirationMs = jwtExpirationMs;
    }

    /**
     * Génère un JWT avec des claims supplémentaires et un pseudo comme sujet.
     *
     * @param extraClaims claims supplémentaires à inclure
     * @param pseudo      pseudo de l'utilisateur (subject)
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
     * Génère un JWT minimal avec le pseudo et l'ID de l'utilisateur.
     */
    public String generateToken(String pseudo, Long userId) {
        Map<String, Object> claims = Map.of(
                "id", userId
        );
        return generateToken(claims, pseudo);
    }

    /**
     * Extrait une information spécifique d'un JWT en utilisant une fonction.
     * @param token token JWT
     * @param claimsResolver fonction pour extraire la valeur désirée depuis les claims
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrait le pseudo de l'utilisateur depuis le JWT.
     */
    public String extractPseudo(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Vérifie si le token est valide pour un pseudo donné
     */
    public boolean isTokenValid(String token, String pseudo) {
        final String extractedPseudo = extractPseudo(token);
        return (extractedPseudo.equals(pseudo)) && !isTokenExpired(token);
    }

    /**
     * Vérifie si le token est expiré
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrait la date d'expiration du token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Récupère tous les claims du token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Getter pour debug / inspection
     */
    public String getJwtSecret() {
        return this.jwtSecret;
    }

    /**
     * Retourne la clé de signature (utile pour debug)
     */
    public Key getSigningKeyForDebug() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * Méthode exécutée après construction du bean
     * Affiche le secret et la clé pour vérification lors du démarrage
     */
    @PostConstruct
    public void init() {
        System.out.println("🔑 JwtService (auth-service) initialisé avec secret=" + getJwtSecret());
        System.out.println("Clé de signature (auth-service) : " + getSigningKeyForDebug());
    }

}

package com.jdrbibli.gateway.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Repository de contexte de sécurité pour Spring WebFlux utilisant JWT.
 * 
 * Implémente {@link ServerSecurityContextRepository} pour charger le {@link SecurityContext}
 * à partir d'un JWT présent dans le header {@code Authorization} de la requête.
 * 
 * ⚠️ Actuellement, le gateway ne valide plus les JWT, mais cette classe est prête
 * pour la réactivation de l'authentification basée sur JWT.
 */
@Component
public class JwtSecurityContextRepository implements ServerSecurityContextRepository {

    private final JwtAuthenticationManager authenticationManager;

    /**
     * Constructeur qui injecte le {@link JwtAuthenticationManager} utilisé pour valider les tokens.
     *
     * @param authenticationManager gestionnaire d'authentification JWT.
     */
    public JwtSecurityContextRepository(JwtAuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * Sauvegarde le contexte de sécurité. Non implémenté ici car l'authentification est stateless.
     *
     * @param exchange contexte de la requête/response.
     * @param context  contexte de sécurité à sauvegarder.
     * @return un {@link Mono} vide.
     */
    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();
    }

    /**
     * Charge le {@link SecurityContext} à partir du JWT présent dans le header {@code Authorization}.
     *
     * @param exchange contexte de la requête entrante.
     * @return un {@link Mono} contenant le {@link SecurityContext} si le JWT est valide, sinon vide.
     */
    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String authToken = authHeader.substring(7);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(authToken, authToken);
            return authenticationManager.authenticate(auth)
                    .map(SecurityContextImpl::new);
        }
        return Mono.empty();
    }
}

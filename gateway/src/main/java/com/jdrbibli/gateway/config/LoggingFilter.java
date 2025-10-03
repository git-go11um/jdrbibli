package com.jdrbibli.gateway.config;

import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Filtre global pour logger les requêtes entrantes dans le gateway-service.
 * 
 * Ce {@link WebFilter} intercepte toutes les requêtes HTTP avant qu'elles ne soient routées
 * vers les microservices et affiche dans la console :
 * <ul>
 *   <li>la méthode HTTP et l'URI de la requête</li>
 *   <li>le header Authorization (JWT si présent)</li>
 *   <li>le header X-User-Id (identifiant de l'utilisateur si présent)</li>
 * </ul>
 * 
 * Le filtre est exécuté avec une priorité {@link Order} égale à 1.
 */
@Component
@Order(1)
public class LoggingFilter implements WebFilter {

    /**
     * Intercepte la requête entrante, affiche les informations de logging dans la console
     * et continue la chaîne de filtres.
     *
     * @param exchange contexte de la requête et de la réponse HTTP.
     * @param chain    chaîne de filtres à exécuter après ce filtre.
     * @return un {@link Mono} qui complète lorsque la chaîne de filtres est terminée.
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        System.out.println("[Gateway] " + request.getMethod() + " " + request.getURI());
        System.out.println("[Gateway] Authorization: " + request.getHeaders().getFirst("Authorization"));
        System.out.println("[Gateway] X-User-Id: " + request.getHeaders().getFirst("X-User-Id"));

        return chain.filter(exchange);
    }
}

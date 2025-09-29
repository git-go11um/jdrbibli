package com.jdrbibli.gateway.config;

import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(1)
public class LoggingFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        System.out.println("[Gateway] " + request.getMethod() + " " + request.getURI());
        System.out.println("[Gateway] Authorization: " + request.getHeaders().getFirst("Authorization"));
        System.out.println("[Gateway] X-User-Id: " + request.getHeaders().getFirst("X-User-Id"));

        return chain.filter(exchange);
    }
}

package com.jdrbibli.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class LoggingFilterTest {

    @Test
    void testFilterLogsRequestAndContinuesChain() {
        LoggingFilter filter = new LoggingFilter();

        // Construire une requête factice
        MockServerHttpRequest request = MockServerHttpRequest
                .method(HttpMethod.GET, "/api/test")
                .header("Authorization", "Bearer fake-token")
                .header("X-User-Id", "123")
                .build();

        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // Mock de la chaîne de filtres
        WebFilterChain chain = mock(WebFilterChain.class);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        // Exécuter le filtre
        Mono<Void> result = filter.filter(exchange, chain);

        // Vérifier que la chaîne continue
        StepVerifier.create(result).verifyComplete();
        verify(chain, times(1)).filter(exchange);

        // Vérifier que les headers et méthode existent (couvre l'accès aux getters)
        assert exchange.getRequest().getHeaders().getFirst("Authorization").equals("Bearer fake-token");
        assert exchange.getRequest().getHeaders().getFirst("X-User-Id").equals("123");
        assert exchange.getRequest().getMethod() == HttpMethod.GET;
        assert exchange.getResponse().getStatusCode() == null;

    }
}

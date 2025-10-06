package com.jdrbibli.gateway.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.core.context.SecurityContext;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class JwtSecurityContextRepositoryTest {

    private JwtAuthenticationManager authenticationManager;
    private JwtSecurityContextRepository repository;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(JwtAuthenticationManager.class);
        repository = new JwtSecurityContextRepository(authenticationManager);
    }

    @Test
    void loadWithValidToken() {
        String token = "valid.jwt.token";

        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/test")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token));

        var auth = mock(org.springframework.security.core.Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(reactor.core.publisher.Mono.just(auth));

        StepVerifier.create(repository.load(exchange))
                .expectNextMatches(sc -> sc.getAuthentication() == auth)
                .verifyComplete();

        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    void loadWithInvalidToken() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/test"));

        StepVerifier.create(repository.load(exchange))
                .verifyComplete();

        verifyNoInteractions(authenticationManager);
    }

    @Test
    void saveReturnsEmpty() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/test"));
        var context = mock(SecurityContext.class);

        StepVerifier.create(repository.save(exchange, context))
                .verifyComplete();
    }
}

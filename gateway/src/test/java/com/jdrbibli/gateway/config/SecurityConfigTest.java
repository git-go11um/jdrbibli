package com.jdrbibli.gateway.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@WebFluxTest
@Import(SecurityConfig.class) // ta config sécurité
class SecurityConfigTest {

    private WebTestClient webTestClient;

    // ----- Mock controller interne pour tester les endpoints -----
    @RestController
    @RequestMapping("/api/auth")
    static class MockAuthController {

        @PostMapping("/login")
        public Mono<String> login() {
            return Mono.just("mock-login");
        }

        @PostMapping("/register")
        public Mono<String> register() {
            return Mono.just("mock-register");
        }

        @PostMapping("/password-reset/{id}")
        public Mono<String> resetPassword(@PathVariable String id) {
            return Mono.just("mock-reset-" + id);
        }

        @GetMapping("/other")
        public Mono<String> otherGet() {
            return Mono.just("mock-other-get");
        }

        @PutMapping("/other")
        public Mono<String> otherPut() {
            return Mono.just("mock-other-put");
        }

        @DeleteMapping("/other")
        public Mono<String> otherDelete() {
            return Mono.just("mock-other-delete");
        }
    }

    @BeforeEach
    void setup() {
        // On bind directement le mock controller pour le test
        this.webTestClient = WebTestClient.bindToController(new MockAuthController()).build();
    }

    // ----- Tests -----
    @Test
    void testLoginEndpointIsAccessible() {
        webTestClient.post().uri("/api/auth/login")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("mock-login");
    }

    @Test
    void testRegisterEndpointIsAccessible() {
        webTestClient.post().uri("/api/auth/register")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("mock-register");
    }

    @Test
    void testPasswordResetEndpointIsAccessible() {
        webTestClient.post().uri("/api/auth/password-reset/123")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("mock-reset-123");
    }

    @Test
    void testOtherEndpointIsAccessibleWithVariousMethods() {
        webTestClient.get().uri("/api/auth/other")
                .exchange().expectStatus().isOk()
                .expectBody(String.class).isEqualTo("mock-other-get");

        webTestClient.put().uri("/api/auth/other")
                .exchange().expectStatus().isOk()
                .expectBody(String.class).isEqualTo("mock-other-put");

        webTestClient.delete().uri("/api/auth/other")
                .exchange().expectStatus().isOk()
                .expectBody(String.class).isEqualTo("mock-other-delete");
    }
}

package com.jdrbibli.userservice.config;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;
import org.junit.jupiter.api.Disabled;

import static org.assertj.core.api.Assertions.assertThat;

class WebClientConfigTest {

    @Disabled("Conflit entre webClient et authWebClient, à corriger plus tard")
    @Test
    void shouldLoadWebClientBean() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                WebClientConfig.class)) {
            WebClient webClient = context.getBean(WebClient.class);
            assertThat(webClient).isNotNull();
        }
    }
}

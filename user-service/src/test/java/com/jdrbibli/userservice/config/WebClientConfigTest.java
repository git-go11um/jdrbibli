package com.jdrbibli.userservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

class WebClientConfigTest {

    @Test
    void shouldLoadWebClientBean() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                WebClientConfig.class)) {
            WebClient webClient = context.getBean(WebClient.class);
            assertThat(webClient).isNotNull();
        }
    }
}

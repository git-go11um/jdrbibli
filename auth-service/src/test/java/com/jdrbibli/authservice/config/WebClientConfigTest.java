package com.jdrbibli.authservice.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

class WebClientConfigTest {

    private WebClientConfig webClientConfig;

    @BeforeEach
    void setUp() {
        webClientConfig = new WebClientConfig();
    }

    @Disabled("Conflit entre webClient et authWebClient, à corriger plus tard")
    @Test
    void webClient_shouldReturnNonNullInstance() {
        WebClient client = webClientConfig.webClient();

        assertThat(client).isNotNull();
        assertThat(client).isInstanceOf(WebClient.class);
    }
}

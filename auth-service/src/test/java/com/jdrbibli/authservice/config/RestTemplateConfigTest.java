package com.jdrbibli.authservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class RestTemplateConfigTest {

    @Test
    void testRestTemplateBeanNotNull() {
        RestTemplateConfig config = new RestTemplateConfig();
        RestTemplate restTemplate = config.restTemplate();

        assertNotNull(restTemplate, "Le RestTemplate ne doit pas être null");
        assertTrue(restTemplate instanceof RestTemplate, "Doit être une instance de RestTemplate");
    }

    @Test
    void testRestTemplateMultipleCalls() {
        RestTemplateConfig config = new RestTemplateConfig();

        RestTemplate firstCall = config.restTemplate();
        RestTemplate secondCall = config.restTemplate();

        assertNotNull(firstCall);
        assertNotNull(secondCall);

        // On ne force pas l'égalité d'instances, mais on vérifie que ce sont bien deux
        // RestTemplate valides
        assertEquals(RestTemplate.class, firstCall.getClass());
        assertEquals(RestTemplate.class, secondCall.getClass());
    }
}

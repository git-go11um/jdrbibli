package com.jdrbibli.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class RestTemplateTest {

    @Test
    void restTemplateBean_shouldBeCreated() {
        RestTemplate restTemplate = new RestTemplate();
        assertNotNull(restTemplate, "Le bean RestTemplate doit être créé");
    }
}

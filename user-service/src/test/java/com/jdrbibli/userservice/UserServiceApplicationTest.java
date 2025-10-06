package com.jdrbibli.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceApplicationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads_shouldStartApplicationContext() {
        assertNotNull(context, "Le contexte Spring devrait être chargé");
    }

    @Test
    void restTemplateBean_shouldBeCreated() {
        RestTemplate restTemplate = context.getBean(RestTemplate.class);
        assertNotNull(restTemplate, "Le bean RestTemplate doit être créé");
    }
}

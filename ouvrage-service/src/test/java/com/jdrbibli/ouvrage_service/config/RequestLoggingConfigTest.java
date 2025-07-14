package com.jdrbibli.ouvrage_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RequestLoggingConfigTest {

    @Autowired
    private CommonsRequestLoggingFilter logFilter;

    @Test
    void testLogFilterBeanExists() {
        assertNotNull(logFilter, "Le bean CommonsRequestLoggingFilter doit être créé");
    }
}

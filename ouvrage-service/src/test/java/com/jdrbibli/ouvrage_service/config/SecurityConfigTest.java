package com.jdrbibli.ouvrage_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

    @Test
    void securityConfig_shouldLoadInSpringContext() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                SecurityConfig.class)) {

            SecurityConfig config = context.getBean(SecurityConfig.class);

            assertThat(config).isNotNull();
        }
    }
}

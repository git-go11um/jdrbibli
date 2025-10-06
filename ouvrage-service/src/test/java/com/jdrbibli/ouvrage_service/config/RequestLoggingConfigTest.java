package com.jdrbibli.ouvrage_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RequestLoggingConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void logFilterBean_shouldBeCreated() {
        CommonsRequestLoggingFilter filter = applicationContext.getBean(CommonsRequestLoggingFilter.class);
        assertThat(filter).isNotNull();
        assertThat(filter).isInstanceOf(CommonsRequestLoggingFilter.class);
    }
}

package com.jdrbibli.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.cors.reactive.CorsWebFilter;

import static org.junit.jupiter.api.Assertions.*;

class CorsConfigTest {

    @Test
    void testCorsWebFilterIsCreated() {
        CorsConfig corsConfig = new CorsConfig();
        CorsWebFilter filter = corsConfig.corsWebFilter();

        assertNotNull(filter, "Le bean CorsWebFilter doit être créé");
    }
}

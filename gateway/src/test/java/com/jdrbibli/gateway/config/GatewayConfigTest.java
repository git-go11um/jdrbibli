/* package com.jdrbibli.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class GatewayConfigTest {

    @Test
    void testCustomRouteLocator() {
        RouteLocatorBuilder builder = mock(RouteLocatorBuilder.class, RETURNS_DEEP_STUBS);
        GatewayConfig config = new GatewayConfig();

        // Ici on ne teste que que le bean est créé, pas les routes concrètes
        RouteLocator locator = config.customRouteLocator(builder);

        assertNotNull(locator, "Le RouteLocator ne doit pas être null");
    }
}
 */
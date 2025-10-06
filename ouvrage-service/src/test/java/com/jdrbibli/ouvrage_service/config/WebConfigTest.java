package com.jdrbibli.ouvrage_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WebConfigTest {

    @Test
    void addResourceHandlers_shouldRegisterUploadsImagesHandler() {
        WebConfig webConfig = new WebConfig();

        // Mock du registry
        ResourceHandlerRegistry registry = mock(ResourceHandlerRegistry.class);

        // Mock du ResourceHandlerRegistration (retour de addResourceHandler)
        ResourceHandlerRegistration registration = mock(ResourceHandlerRegistration.class);

        // Quand addResourceHandler est appelé, on retourne notre mock de registration
        when(registry.addResourceHandler("/uploads/images/**")).thenReturn(registration);

        // On appelle la méthode à tester
        webConfig.addResourceHandlers(registry);

        // Vérifications
        verify(registry).addResourceHandler("/uploads/images/**");
        verify(registration).addResourceLocations("file:uploads/images/");
    }
}

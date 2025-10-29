package com.jdrbibli.ouvrage_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.*;

class WebConfigTest {

    @Test
    void addResourceHandlers_shouldRegisterUploadsImagesHandler() {
        WebConfig webConfig = new WebConfig();

        ResourceHandlerRegistry registry = mock(ResourceHandlerRegistry.class);
        ResourceHandlerRegistration registration = mock(ResourceHandlerRegistration.class);

        when(registry.addResourceHandler("/uploads/images/**")).thenReturn(registration);

        webConfig.addResourceHandlers(registry);

        verify(registry).addResourceHandler("/uploads/images/**");
        // ✅ on rend le test compatible avec tous les chemins commençant par "file:"
        verify(registration).addResourceLocations(startsWith("file:"));
    }
}

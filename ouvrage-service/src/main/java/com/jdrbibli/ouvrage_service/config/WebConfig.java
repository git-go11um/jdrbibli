package com.jdrbibli.ouvrage_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration MVC pour le microservice {@code ouvrage-service}.
 * 
 * Cette classe permet de configurer le mapping des ressources statiques
 * accessibles via HTTP.
 * Actuellement, elle configure un {@link ResourceHandlerRegistry} pour exposer
 * les images
 * stockées localement dans le dossier {@code /app/uploads/images}.
 * 
 * Exemple :
 * <ul>
 * <li>Une image sauvegardée dans {@code /app/uploads/images/mon_image.jpg} sera
 * accessible via
 * {@code http://<server>/uploads/images/mon_image.jpg}</li>
 * </ul>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/images/**")
                .addResourceLocations("file:/app/uploads/images/");
    }
}

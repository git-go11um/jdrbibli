package com.jdrbibli.ouvrage_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration MVC pour le microservice {@code ouvrage-service}.
 * <p>
 * Cette classe permet de configurer le mapping des ressources statiques accessibles via HTTP.
 * Actuellement, elle configure un {@link ResourceHandlerRegistry} pour exposer les images
 * stockées localement dans le dossier {@code uploads/images}.
 * <p>
 * Exemple :
 * <ul>
 *   <li>Une image sauvegardée dans {@code uploads/images/mon_image.jpg} sera accessible via
 *       {@code http://<server>/uploads/images/mon_image.jpg}</li>
 * </ul>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configure les handlers de ressources pour servir les fichiers statiques.
     *
     * @param registry le registre des handlers de ressources.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/images/**")
                .addResourceLocations("file:uploads/images/");
    }
}

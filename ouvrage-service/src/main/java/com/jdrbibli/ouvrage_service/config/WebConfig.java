package com.jdrbibli.ouvrage_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Sert les fichiers depuis le dossier uploads/images/ sur le serveur
        registry.addResourceHandler("/uploads/images/**")
                .addResourceLocations("file:uploads/images/");
    }
}

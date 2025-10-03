package com.jdrbibli.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Classe principale du microservice UserService.
 * 
 * Cette classe démarre l'application Spring Boot et configure les beans globaux
 * tels que {@link RestTemplate} pour permettre les appels HTTP vers d'autres services.
 * 
 */
@SpringBootApplication
public class UserServiceApplication {

    /**
     * Point d'entrée de l'application UserService.
     *
     * @param args arguments de la ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    /**
     * Bean {@link RestTemplate} utilisé pour effectuer des appels HTTP synchrones vers
     * d'autres microservices.
     *
     * @return un objet {@link RestTemplate} configuré
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

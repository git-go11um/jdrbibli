package com.jdrbibli.authservice.jpa;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.jdrbibli.authservice.repository")
@EntityScan(basePackages = "com.jdrbibli.authservice.entity")
public class TestJpaConfig {
    // Configuration minimale pour scanner uniquement les entités et les repositories
}

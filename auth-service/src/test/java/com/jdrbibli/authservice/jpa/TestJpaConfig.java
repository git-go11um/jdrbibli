package com.jdrbibli.authservice.jpa;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("com.jdrbibli.authservice.entity")
@EnableJpaRepositories("com.jdrbibli.authservice.repository")
public class TestJpaConfig {
}

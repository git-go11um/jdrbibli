package com.jdrbibli.authservice.jpa;

import com.jdrbibli.authservice.client.AuditClient;
import com.jdrbibli.authservice.repository.UserRepository;
import com.jdrbibli.authservice.security.JwtTokenProvider;
import com.jdrbibli.authservice.service.EmailService;
import com.jdrbibli.authservice.service.UserService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public UserService userService() {
        return mock(UserService.class);
    }

    @Bean
    @Primary
    public UserRepository userRepository() {
        return mock(UserRepository.class);
    }

    @Bean
    @Primary
    public AuditClient auditClient() {
        return mock(AuditClient.class);
    }

    @Bean
    @Primary
    public JwtTokenProvider jwtTokenProvider() {
        return mock(JwtTokenProvider.class);
    }

    @Bean
    @Primary
    public EmailService emailService() {
        return mock(EmailService.class);
    }
}

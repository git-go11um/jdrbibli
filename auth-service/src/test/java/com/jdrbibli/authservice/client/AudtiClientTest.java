package com.jdrbibli.authservice.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuditClientTest {

    private AuditClient auditClient;
    private RestTemplate mockRestTemplate;

    @BeforeEach
    void setUp() {
        auditClient = new AuditClient();
        mockRestTemplate = Mockito.mock(RestTemplate.class);

        // On injecte notre RestTemplate mocké à la place du vrai
        ReflectionTestUtils.setField(auditClient, "restTemplate", mockRestTemplate);
        ReflectionTestUtils.setField(auditClient, "auditUrl", "http://localhost:9999/api/audit/logs");
    }

    @Test
    void testLogEvent_success() {
        // Arrange
        ResponseEntity<String> fakeResponse = new ResponseEntity<>("OK", HttpStatus.CREATED);
        when(mockRestTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(fakeResponse);

        // Act
        auditClient.logEvent("auth-service", "login", "User connected");

        // Assert : vérifie que le post a bien été appelé une fois
        verify(mockRestTemplate, times(1))
                .postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void testLogEvent_failure() {
        // Arrange : simuler une exception réseau
        when(mockRestTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        // Act : pas d’exception à remonter car gérée dans la méthode
        auditClient.logEvent("auth-service", "login", "User failed");

        // Assert : on vérifie juste que l’appel a eu lieu
        verify(mockRestTemplate, times(1))
                .postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
    }


}

package com.jdrbibli.authservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class AuditClient {
    private final Logger log = LoggerFactory.getLogger(AuditClient.class);
    private final RestTemplate restTemplate = new RestTemplate();

    // configurable via application.yml
    @Value("${app.audit-url:http://localhost:8085/api/audit/logs}")
    private String auditUrl;

    public void logEvent(String serviceName, String action, String details) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("serviceName", serviceName);
            body.put("action", action);
            body.put("details", details);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(auditUrl, request, String.class);

            log.info("[AuditClient] sent event {} -> status {}", action, response.getStatusCode());
        } catch (Exception e) {
            log.warn("[AuditClient] failed to send audit event: {}", e.toString());
            log.debug("stack:", e);
        }
    }
}

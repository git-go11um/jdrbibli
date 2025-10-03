package com.jdrbibli.authservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Client pour envoyer des événements d'audit vers le service Audit.
 * <p>
 * Cette classe utilise Spring {@link RestTemplate} pour faire des requêtes HTTP POST
 * vers l'URL configurée pour le service d'audit. Les événements envoyés contiennent 
 * le nom du service, l'action effectuée et des détails supplémentaires.
 * </p>
 * <p>
 * L'URL du service d'audit est configurable via la propriété `app.audit-url` dans `application.yml`.
 * Si elle n'est pas définie, la valeur par défaut est `http://localhost:8085/api/audit/logs`.
 * </p>
 */
@Component
public class AuditClient {
    private final Logger log = LoggerFactory.getLogger(AuditClient.class);
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * URL du service Audit.
     * Configurable via la propriété `app.audit-url`.
     */
    @Value("${app.audit-url:http://localhost:8085/api/audit/logs}")
    private String auditUrl;

    /**
     * Envoie un événement d'audit au service Audit.
     *
     * @param serviceName le nom du service générant l'événement (ex: "auth-service")
     * @param action      l'action effectuée (ex: "login", "password-reset")
     * @param details     des informations supplémentaires sur l'événement
     */
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

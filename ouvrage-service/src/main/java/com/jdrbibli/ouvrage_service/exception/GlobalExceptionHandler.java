package com.jdrbibli.ouvrage_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Gestionnaire global des exceptions pour le microservice ouvrage-service.
 *
 * Capture toutes les exceptions levées par les contrôleurs et retourne
 * un corps JSON standardisé, tout en affichant le stacktrace dans les logs.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gère les exceptions de type ResourceNotFoundException.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex) {
        logException(ex);
        return buildResponseEntity(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage());
    }

    /**
     * Gère les exceptions de type RuntimeException.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        logException(ex);
        return buildResponseEntity(HttpStatus.BAD_REQUEST, "Runtime Error", ex.getMessage());
    }

    /**
     * Gère toutes les autres exceptions non capturées spécifiquement.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        logException(ex);
        String message = (ex.getMessage() != null) ? ex.getMessage() : ex.getClass().getSimpleName();
        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", message);
    }

    /**
     * Construit un corps JSON standard pour la réponse.
     */
    private ResponseEntity<Object> buildResponseEntity(HttpStatus status, String error, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);

        return new ResponseEntity<>(body, status);
    }

    /**
     * Loggue proprement l’exception complète dans la console.
     */
    private void logException(Exception ex) {
        System.err.println("❌ Exception interceptée : " + ex.getClass().getSimpleName());
        if (ex.getMessage() != null) {
            System.err.println("🧾 Message : " + ex.getMessage());
        }
        ex.printStackTrace(System.err);
        System.err.println("------------------------------------------------------------");
    }
}

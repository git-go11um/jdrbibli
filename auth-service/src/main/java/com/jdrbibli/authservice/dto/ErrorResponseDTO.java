package com.jdrbibli.authservice.dto;

import java.time.LocalDateTime;

public class ErrorResponseDTO {
    private String message;
    private LocalDateTime timestamp;
    private int status;

    public ErrorResponseDTO(String message, int status) {
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    // Getters (pas forcément besoin de setters ici)

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }
}

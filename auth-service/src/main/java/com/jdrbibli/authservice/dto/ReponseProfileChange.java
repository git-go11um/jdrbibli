package com.jdrbibli.authservice.dto;

public class ReponseProfileChange {
    private String message;

    public ReponseProfileChange() {
    }

    public ReponseProfileChange(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

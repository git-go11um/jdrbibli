package com.jdrbibli.authservice.dto;

public class AuthenticationResponse {
    private String token;
    private UserResponseDTO user;

    public AuthenticationResponse() {
    }

    public AuthenticationResponse(String token, UserResponseDTO user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserResponseDTO getUser() {
        return user;
    }

    public void setUser(UserResponseDTO user) {
        this.user = user;
    }
}

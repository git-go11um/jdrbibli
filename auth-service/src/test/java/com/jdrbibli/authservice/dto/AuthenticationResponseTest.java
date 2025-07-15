package com.jdrbibli.authservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class AuthenticationResponseTest {

    @Test
    void testConstructorAndGetters() {
        UserResponseDTO userDto = new UserResponseDTO();
        userDto.setPseudo("testUser");
        userDto.setEmail("test@example.com");

        AuthenticationResponse response = new AuthenticationResponse("jwt-token-123", userDto);

        assertEquals("jwt-token-123", response.getToken());
        assertEquals(userDto, response.getUser());
        assertEquals("testUser", response.getUser().getPseudo());
        assertEquals("test@example.com", response.getUser().getEmail());
    }

    @Test
    void testSetters() {
        AuthenticationResponse response = new AuthenticationResponse();

        UserResponseDTO userDto = new UserResponseDTO();
        userDto.setPseudo("pseudoSetter");
        userDto.setEmail("setter@example.com");

        response.setToken("tokenSetter");
        response.setUser(userDto);

        assertEquals("tokenSetter", response.getToken());
        assertEquals(userDto, response.getUser());
        assertEquals("pseudoSetter", response.getUser().getPseudo());
        assertEquals("setter@example.com", response.getUser().getEmail());
    }
}

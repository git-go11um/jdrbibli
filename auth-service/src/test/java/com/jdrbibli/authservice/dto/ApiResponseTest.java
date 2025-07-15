package com.jdrbibli.authservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ApiResponseTest {

    @Test
    void testConstructorWithMessageOnly() {
        ApiResponse response = new ApiResponse("Operation réussie");

        assertEquals("Operation réussie", response.getMessage());
        assertTrue(response.isSuccess());
        assertNull(response.getToken());
    }

    @Test
    void testConstructorWithAllFields() {
        ApiResponse response = new ApiResponse("Auth OK", true, "jwt-token-123");

        assertEquals("Auth OK", response.getMessage());
        assertTrue(response.isSuccess());
        assertEquals("jwt-token-123", response.getToken());

        ApiResponse failResponse = new ApiResponse("Erreur", false, null);

        assertEquals("Erreur", failResponse.getMessage());
        assertFalse(failResponse.isSuccess());
        assertNull(failResponse.getToken());
    }

    @Test
    void testSettersAndGetters() {
        ApiResponse response = new ApiResponse("Init");

        response.setMessage("Modifié");
        response.setSuccess(false);
        response.setToken("token-modifie");

        assertEquals("Modifié", response.getMessage());
        assertFalse(response.isSuccess());
        assertEquals("token-modifie", response.getToken());
    }
}

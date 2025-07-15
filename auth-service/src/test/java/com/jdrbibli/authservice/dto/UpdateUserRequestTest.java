package com.jdrbibli.authservice.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class UpdateUserRequestTest {

    @Test
    void testGettersAndSetters() {
        UpdateUserRequest request = new UpdateUserRequest();

        request.setPseudo("nouveauPseudo");
        request.setEmail("nouveau.email@example.com");

        assertEquals("nouveauPseudo", request.getPseudo());
        assertEquals("nouveau.email@example.com", request.getEmail());
    }
}

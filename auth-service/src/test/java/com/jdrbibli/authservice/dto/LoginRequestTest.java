package com.jdrbibli.authservice.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class LoginRequestTest {

    @Test
    void testGettersAndSetters() {
        LoginRequest loginRequest = new LoginRequest();

        loginRequest.setPseudo("monPseudo");
        loginRequest.setMotDePasse("monMotDePasse");

        assertEquals("monPseudo", loginRequest.getPseudo());
        assertEquals("monMotDePasse", loginRequest.getMotDePasse());
    }
}

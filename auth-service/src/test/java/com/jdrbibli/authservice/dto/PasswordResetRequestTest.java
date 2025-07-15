package com.jdrbibli.authservice.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class PasswordResetRequestTest {

    @Test
    void testGettersAndSetters() {
        PasswordResetRequest request = new PasswordResetRequest();

        request.setPseudo("pseudoTest");
        request.setCode("code123");
        request.setNewPassword("newPassword!@#");

        assertEquals("pseudoTest", request.getPseudo());
        assertEquals("code123", request.getCode());
        assertEquals("newPassword!@#", request.getNewPassword());
    }
}

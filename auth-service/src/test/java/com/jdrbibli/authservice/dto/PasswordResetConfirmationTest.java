package com.jdrbibli.authservice.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class PasswordResetConfirmationTest {

    @Test
    void testConstructorAndGetters() {
        PasswordResetConfirmation prc = new PasswordResetConfirmation(
                "userPseudo",
                "resetCode123",
                "tokenXYZ",
                "newPass456");

        assertEquals("userPseudo", prc.getPseudo());
        assertEquals("resetCode123", prc.getCode());
        assertEquals("tokenXYZ", prc.getToken());
        assertEquals("newPass456", prc.getNewPassword());
    }

    @Test
    void testSetters() {
        PasswordResetConfirmation prc = new PasswordResetConfirmation();

        prc.setPseudo("pseudoSetter");
        prc.setCode("codeSetter");
        prc.setToken("tokenSetter");
        prc.setNewPassword("passSetter");

        assertEquals("pseudoSetter", prc.getPseudo());
        assertEquals("codeSetter", prc.getCode());
        assertEquals("tokenSetter", prc.getToken());
        assertEquals("passSetter", prc.getNewPassword());
    }
}

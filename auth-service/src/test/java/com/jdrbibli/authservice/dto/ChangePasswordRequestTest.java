package com.jdrbibli.authservice.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ChangePasswordRequestTest {

    @Test
    void testGettersAndSetters() {
        ChangePasswordRequest request = new ChangePasswordRequest();

        request.setNewPassword("newPass123");
        request.setConfirmNewPassword("newPass123");

        assertEquals("newPass123", request.getNewPassword());
        assertEquals("newPass123", request.getConfirmNewPassword());
    }
}

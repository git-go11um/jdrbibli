package com.jdrbibli.authservice.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ChangePasswordProfileRequestTest {

    @Test
    void testGettersAndSetters() {
        ChangePasswordProfileRequest request = new ChangePasswordProfileRequest();

        request.setCurrentPassword("oldPass123");
        request.setNewPassword("newPass456");
        request.setConfirmNewPassword("newPass456");

        assertEquals("oldPass123", request.getCurrentPassword());
        assertEquals("newPass456", request.getNewPassword());
        assertEquals("newPass456", request.getConfirmNewPassword());
    }
}

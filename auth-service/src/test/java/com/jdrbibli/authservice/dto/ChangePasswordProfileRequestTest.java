package com.jdrbibli.authservice.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ChangePasswordProfileRequestTest {

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        ChangePasswordProfileRequest request = new ChangePasswordProfileRequest();

        request.setCurrentPassword("oldPass");
        request.setNewPassword("newPass");
        request.setConfirmNewPassword("newPass");

        assertThat(request.getCurrentPassword()).isEqualTo("oldPass");
        assertThat(request.getNewPassword()).isEqualTo("newPass");
        assertThat(request.getConfirmNewPassword()).isEqualTo("newPass");
    }
}

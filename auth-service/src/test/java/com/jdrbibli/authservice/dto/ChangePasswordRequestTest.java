package com.jdrbibli.authservice.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ChangePasswordRequestTest {

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        ChangePasswordRequest request = new ChangePasswordRequest();

        request.setNewPassword("newPass");
        request.setConfirmNewPassword("newPass");

        assertThat(request.getNewPassword()).isEqualTo("newPass");
        assertThat(request.getConfirmNewPassword()).isEqualTo("newPass");
    }
}

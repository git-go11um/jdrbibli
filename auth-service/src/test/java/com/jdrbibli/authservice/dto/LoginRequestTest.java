package com.jdrbibli.authservice.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestTest {

    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
        LoginRequest request = new LoginRequest();

        request.setPseudo("testUser");
        request.setPassword("password123");

        assertThat(request.getPseudo()).isEqualTo("testUser");
        assertThat(request.getPassword()).isEqualTo("password123");
    }
}

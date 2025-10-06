package com.jdrbibli.authservice.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticationResponseTest {

    @Test
    void defaultConstructor_shouldCreateEmptyObject() {
        AuthenticationResponse response = new AuthenticationResponse();

        assertThat(response.getToken()).isNull();
        assertThat(response.getUser()).isNull();
    }

    @Test
    void fullConstructor_shouldInitializeFields() {
        UserResponseDTO userDto = new UserResponseDTO();
        userDto.setPseudo("user123");
        userDto.setEmail("user@test.com");

        AuthenticationResponse response = new AuthenticationResponse("jwt-token", userDto);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getUser()).isEqualTo(userDto);
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        AuthenticationResponse response = new AuthenticationResponse();

        UserResponseDTO userDto = new UserResponseDTO();
        userDto.setPseudo("pseudo");
        userDto.setEmail("email@test.com");

        response.setToken("nouveau-token");
        response.setUser(userDto);

        assertThat(response.getToken()).isEqualTo("nouveau-token");
        assertThat(response.getUser()).isEqualTo(userDto);
    }
}

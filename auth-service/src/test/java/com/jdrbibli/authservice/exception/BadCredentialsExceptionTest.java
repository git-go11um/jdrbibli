package com.jdrbibli.authservice.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BadCredentialsExceptionTest {

    @Test
    void testExceptionMessage() {
        String msg = "Pseudo ou mot de passe incorrect";

        assertThatThrownBy(() -> {
            throw new BadCredentialsException(msg);
        })
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage(msg);
    }
}

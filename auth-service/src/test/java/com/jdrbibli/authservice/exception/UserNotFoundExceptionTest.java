package com.jdrbibli.authservice.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserNotFoundExceptionTest {

    @Test
    void constructor_shouldSetMessageCorrectly() {
        String message = "Utilisateur non trouvé";

        assertThatThrownBy(() -> {
            throw new UserNotFoundException(message);
        })
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage(message);
    }
}

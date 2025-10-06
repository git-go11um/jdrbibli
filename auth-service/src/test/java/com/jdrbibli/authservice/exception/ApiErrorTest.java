package com.jdrbibli.authservice.exception;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ApiErrorTest {

    @Test
    void testConstructorAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        ApiError error = new ApiError(404, "Not Found", now);

        assertThat(error.getStatus()).isEqualTo(404);
        assertThat(error.getMessage()).isEqualTo("Not Found");
        assertThat(error.getTimestamp()).isEqualTo(now);
    }

    @Test
    void testSetters() {
        LocalDateTime now = LocalDateTime.now();
        ApiError error = new ApiError(0, null, null);

        error.setStatus(500);
        error.setMessage("Internal Server Error");
        error.setTimestamp(now);

        assertThat(error.getStatus()).isEqualTo(500);
        assertThat(error.getMessage()).isEqualTo("Internal Server Error");
        assertThat(error.getTimestamp()).isEqualTo(now);
    }
}

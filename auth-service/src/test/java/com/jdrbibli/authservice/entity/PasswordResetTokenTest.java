package com.jdrbibli.authservice.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordResetTokenTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        User user = new User();
        user.setId(1L);
        user.setPseudo("testUser");

        LocalDateTime expiry = LocalDateTime.now().plusHours(1);
        PasswordResetToken token = new PasswordResetToken("token123", expiry, user);

        assertThat(token.getToken()).isEqualTo("token123");
        assertThat(token.getExpiryDate()).isEqualTo(expiry);
        assertThat(token.getUser()).isEqualTo(user);
        assertThat(token.getUser().getPseudo()).isEqualTo("testUser");
    }

    @Test
    void testDefaultConstructorAndSetters() {
        User user = new User();
        user.setId(2L);
        user.setPseudo("anotherUser");

        LocalDateTime expiry = LocalDateTime.now().plusDays(1);
        PasswordResetToken token = new PasswordResetToken();
        token.setId(10L);
        token.setToken("myToken");
        token.setExpiryDate(expiry);
        token.setUser(user);

        assertThat(token.getId()).isEqualTo(10L);
        assertThat(token.getToken()).isEqualTo("myToken");
        assertThat(token.getExpiryDate()).isEqualTo(expiry);
        assertThat(token.getUser()).isEqualTo(user);
        assertThat(token.getUser().getPseudo()).isEqualTo("anotherUser");
    }

    @Test
    void testToString() {
        User user = new User();
        user.setPseudo("userToString");
        PasswordResetToken token = new PasswordResetToken("tok", LocalDateTime.now(), user);

        String str = token.toString();
        assertThat(str).contains("tok");
        assertThat(str).contains("userToString");
    }
}

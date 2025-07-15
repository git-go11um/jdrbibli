
package com.jdrbibli.authservice.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class PasswordResetTokenTest {

    @Test
    void testConstructorAndGetters() {
        User user = new User();
        user.setPseudo("testUser");

        String tokenStr = "abc123";
        LocalDateTime expiry = LocalDateTime.now().plusHours(1);

        PasswordResetToken token = new PasswordResetToken(tokenStr, expiry, user);

        assertNull(token.getId(), "Id should be null initially");
        assertEquals(tokenStr, token.getToken());
        assertEquals(expiry, token.getExpiryDate());
        assertEquals(user, token.getUser());
    }

    @Test
    void testSetters() {
        PasswordResetToken token = new PasswordResetToken();

        token.setId(10L);
        token.setToken("tokenXYZ");
        LocalDateTime expiry = LocalDateTime.now().plusDays(1);
        token.setExpiryDate(expiry);

        User user = new User();
        user.setPseudo("pseudoSetter");
        token.setUser(user);

        assertEquals(10L, token.getId());
        assertEquals("tokenXYZ", token.getToken());
        assertEquals(expiry, token.getExpiryDate());
        assertEquals(user, token.getUser());
    }

    @Test
    void testToString() {
        User user = new User();
        user.setPseudo("pseudoToString");

        PasswordResetToken token = new PasswordResetToken("toto", LocalDateTime.of(2025, 7, 14, 15, 0), user);

        String toString = token.toString();

        assertTrue(toString.contains("toto"));
        assertTrue(toString.contains("pseudoToString"));
        assertTrue(toString.contains("2025-07-14T15:00"));
    }
}

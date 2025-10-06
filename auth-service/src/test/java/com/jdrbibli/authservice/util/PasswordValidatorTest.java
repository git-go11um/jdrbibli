package com.jdrbibli.authservice.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {

    @Test
    void testValidPassword() {
        String validPassword = "Abcdef1!";
        assertDoesNotThrow(() -> PasswordValidator.validate(validPassword));
    }

    @Test
    void testTooShortPassword() {
        String shortPassword = "Ab1!";
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> PasswordValidator.validate(shortPassword));
        assertEquals("Le mot de passe doit contenir au moins 8 caractères.", ex.getMessage());
    }

    @Test
    void testNoUppercase() {
        String password = "abcdef1!";
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> PasswordValidator.validate(password));
        assertEquals("Le mot de passe doit contenir au moins une lettre majuscule.", ex.getMessage());
    }

    @Test
    void testNoLowercase() {
        String password = "ABCDEF1!";
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> PasswordValidator.validate(password));
        assertEquals("Le mot de passe doit contenir au moins une lettre minuscule.", ex.getMessage());
    }

    @Test
    void testNoDigit() {
        String password = "Abcdefg!";
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> PasswordValidator.validate(password));
        assertEquals("Le mot de passe doit contenir au moins un chiffre.", ex.getMessage());
    }

    @Test
    void testNoSpecialChar() {
        String password = "Abcdef12";
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> PasswordValidator.validate(password));
        assertEquals("Le mot de passe doit contenir au moins un caractère spécial (par ex. !@#$%^&*).",
                ex.getMessage());
    }

    @Test
    void testContainsSpace() {
        String password = "Abc def1!";
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> PasswordValidator.validate(password));
        assertEquals("Le mot de passe ne doit pas contenir d'espaces.", ex.getMessage());
    }

    @Test
    void testNullPassword() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> PasswordValidator.validate(null));
        assertEquals("Le mot de passe doit contenir au moins 8 caractères.", ex.getMessage());
    }
}

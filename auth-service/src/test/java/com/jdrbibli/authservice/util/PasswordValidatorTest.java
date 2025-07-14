package com.jdrbibli.authservice.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {

    @Test
    void testValidPassword() {
        assertDoesNotThrow(() -> PasswordValidator.validate("Valid1!Password"));
    }

    @Test
    void testPasswordTooShort() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> PasswordValidator.validate("Ab1!"));
        assertTrue(ex.getMessage().contains("au moins 8 caractères"));
    }

    @Test
    void testMissingUppercase() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> PasswordValidator.validate("valid1!password"));
        assertTrue(ex.getMessage().contains("au moins une lettre majuscule"));
    }

    @Test
    void testMissingLowercase() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> PasswordValidator.validate("VALID1!PASSWORD"));
        assertTrue(ex.getMessage().contains("au moins une lettre minuscule"));
    }

    @Test
    void testMissingDigit() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> PasswordValidator.validate("Valid!Password"));
        assertTrue(ex.getMessage().contains("au moins un chiffre"));
    }

    @Test
    void testMissingSpecialCharacter() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> PasswordValidator.validate("Valid1Password"));
        assertTrue(ex.getMessage().contains("au moins un caractère spécial"));
    }

    @Test
    void testContainsSpace() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> PasswordValidator.validate("Valid1! Pass"));
        assertTrue(ex.getMessage().contains("ne doit pas contenir d'espaces"));
    }
}

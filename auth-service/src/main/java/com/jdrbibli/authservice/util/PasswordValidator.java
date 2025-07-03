package com.jdrbibli.authservice.util;

public class PasswordValidator {

    private static final int MIN_LENGTH = 8;

    public static void validate(String password) {
        if (password == null || password.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins " + MIN_LENGTH + " caractères.");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins une lettre majuscule.");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins une lettre minuscule.");
        }
        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins un chiffre.");
        }
        if (password.contains(" ")) {
            throw new IllegalArgumentException("Le mot de passe ne doit pas contenir d'espaces.");
        }
        // optionnel : if (!password.matches(".*[!@#$%^&*].*")) {...}
    }
}

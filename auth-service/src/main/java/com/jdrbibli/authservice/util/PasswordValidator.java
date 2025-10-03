package com.jdrbibli.authservice.util;

/**
 * Utilitaire de validation des mots de passe.
 * <p>
 * Cette classe fournit une méthode statique {@link #validate(String)} qui
 * permet de vérifier
 * qu'un mot de passe respecte un ensemble de règles de sécurité minimales :
 * <ul>
 * <li>au moins {@value MIN_LENGTH} caractères</li>
 * <li>au moins une lettre majuscule</li>
 * <li>au moins une lettre minuscule</li>
 * <li>au moins un chiffre</li>
 * <li>au moins un caractère spécial parmi {@code !@#$%^&*}</li>
 * <li>aucun espace</li>
 * </ul>
 * Si l'une des conditions n'est pas respectée, une
 * {@link IllegalArgumentException} est levée.
 */
public class PasswordValidator {

    /** Longueur minimale exigée pour un mot de passe valide. */
    private static final int MIN_LENGTH = 8;

    /**
     * Valide un mot de passe en fonction des critères de sécurité définis.
     *
     * @param password le mot de passe à valider.
     * @throws IllegalArgumentException si le mot de passe ne respecte pas les
     *                                  règles :
     *                                  <ul>
     *                                  <li>au moins {@value MIN_LENGTH}
     *                                  caractères</li>
     *                                  <li>au moins une majuscule</li>
     *                                  <li>au moins une minuscule</li>
     *                                  <li>au moins un chiffre</li>
     *                                  <li>au moins un caractère spécial parmi
     *                                  {@code !@#$%^&*}</li>
     *                                  <li>aucun espace</li>
     *                                  </ul>
     */
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
        if (!password.matches(".*[!@#$%^&*].*")) {
            throw new IllegalArgumentException(
                    "Le mot de passe doit contenir au moins un caractère spécial (par ex. !@#$%^&*).");
        }
    }
}

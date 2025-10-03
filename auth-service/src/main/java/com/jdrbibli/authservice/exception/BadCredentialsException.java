package com.jdrbibli.authservice.exception;

/**
 * Exception personnalisée pour gérer les cas où les identifiants
 * (pseudo/mot de passe) fournis par l'utilisateur sont incorrects.
 * <p>
 * Cette exception est lancée principalement lors du processus de login
 * lorsque l'authentification échoue.
 * </p>
 */
public class BadCredentialsException extends RuntimeException {

    /**
     * Constructeur avec message d'erreur.
     *
     * @param message Message décrivant la raison de l'erreur
     */
    public BadCredentialsException(String message) {
        super(message);
    }
}

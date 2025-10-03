package com.jdrbibli.authservice.exception;

/**
 * Exception personnalisée levée lorsque l'utilisateur recherché
 * n'existe pas dans la base de données.
 * <p>
 * Elle étend RuntimeException, donc c'est une exception non vérifiée.
 * Elle est interceptée par GlobalExceptionHandler pour renvoyer un message
 * clair au client avec le statut HTTP 404.
 * </p>
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructeur avec message personnalisé.
     *
     * @param message Le message décrivant l'erreur (ex : "Utilisateur non trouvé")
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}

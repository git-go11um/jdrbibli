package com.jdrbibli.ouvrage_service.exception;
/**
 * Exception levée lorsqu'une ressource existe déjà ou qu'une opération entre en conflit.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

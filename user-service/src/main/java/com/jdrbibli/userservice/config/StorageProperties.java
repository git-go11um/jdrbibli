package com.jdrbibli.userservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Propriétés de configuration pour le stockage des avatars utilisateur.
 * <p>
 * Cette classe permet de lier la propriété YAML/Properties {@code user.avatar.uploadDir}
 * à la variable {@link #uploadDir}. Elle définit le répertoire dans lequel
 * les fichiers avatars seront uploadés.
 */
@Configuration
@ConfigurationProperties(prefix = "user.avatar")
public class StorageProperties {

    /**
     * Répertoire de stockage des fichiers avatars.
     */
    private String uploadDir;

    /**
     * Récupère le répertoire de stockage des avatars.
     *
     * @return le chemin du répertoire d'upload
     */
    public String getUploadDir() {
        return uploadDir;
    }

    /**
     * Définit le répertoire de stockage des avatars.
     *
     * @param uploadDir le chemin du répertoire d'upload
     */
    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}

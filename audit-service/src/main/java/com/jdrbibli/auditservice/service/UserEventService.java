package com.jdrbibli.auditservice.service;

import com.jdrbibli.auditservice.entity.UserEvent;
import com.jdrbibli.auditservice.repository.UserEventRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Service métier pour la gestion des événements utilisateur.
 * 
 * Fournit des méthodes pour enregistrer et récupérer les {@link UserEvent}.
 * Ce service centralise la logique métier liée aux actions des utilisateurs
 * et encapsule l'accès au {@link UserEventRepository}.
 */
@Service
public class UserEventService {

    private final UserEventRepository repository;

    /**
     * Constructeur injectant le repository des événements utilisateur.
     *
     * @param repository repository MongoDB pour les {@link UserEvent}.
     */
    public UserEventService(UserEventRepository repository) {
        this.repository = repository;
    }

    /**
     * Enregistre un nouvel événement utilisateur avec l'heure courante.
     *
     * @param userId    identifiant de l'utilisateur ayant généré l'événement.
     * @param eventType type d'événement (ex. LOGIN, LOGOUT, PASSWORD_CHANGED).
     * @param details   détails optionnels concernant l'événement.
     * @return le {@link UserEvent} sauvegardé en base de données.
     */
    public UserEvent logEvent(Long userId, String eventType, String details) {
        UserEvent event = new UserEvent(
                userId,
                eventType,
                Instant.now(),
                details
        );
        return repository.save(event);
    }

    /**
     * Récupère tous les événements utilisateur enregistrés.
     *
     * @return liste de tous les {@link UserEvent}.
     */
    public List<UserEvent> getAllEvents() {
        return repository.findAll();
    }
}

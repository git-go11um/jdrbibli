package com.jdrbibli.auditservice.controller;

import com.jdrbibli.auditservice.entity.UserEvent;
import com.jdrbibli.auditservice.service.UserEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST permettant de gérer les événements liés aux utilisateurs.
 * <p>
 * Un {@link UserEvent} représente une action spécifique effectuée par un utilisateur
 * (ex. connexion, changement de mot de passe, modification de profil, etc.).
 * Ce contrôleur expose des endpoints pour :
 * <ul>
 *   <li>enregistrer un nouvel événement utilisateur</li>
 *   <li>récupérer la liste des événements enregistrés</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/audit")
public class UserEventController {

    private final UserEventService service;

    /**
     * Constructeur injectant le service de gestion des événements utilisateur.
     *
     * @param service service responsable de la logique métier autour des {@link UserEvent}.
     */
    public UserEventController(UserEventService service) {
        this.service = service;
    }

    /**
     * Enregistre un nouvel événement utilisateur.
     *
     * @param userId    identifiant de l'utilisateur ayant généré l'événement.
     * @param eventType type d'événement (ex. LOGIN, LOGOUT, PASSWORD_CHANGED).
     * @param details   détails optionnels sur l'événement (peut être {@code null}).
     * @return l'événement sauvegardé, encapsulé dans une {@link ResponseEntity}.
     */
    @PostMapping
    public ResponseEntity<UserEvent> logEvent(
            @RequestParam Long userId,
            @RequestParam String eventType,
            @RequestParam(required = false) String details) {
        return ResponseEntity.ok(service.logEvent(userId, eventType, details));
    }

    /**
     * Récupère tous les événements utilisateurs enregistrés.
     *
     * @return une liste de {@link UserEvent}, encapsulée dans une {@link ResponseEntity}.
     */
    @GetMapping
    public ResponseEntity<List<UserEvent>> getAllEvents() {
        return ResponseEntity.ok(service.getAllEvents());
    }
}

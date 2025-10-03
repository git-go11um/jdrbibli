package com.jdrbibli.auditservice.controller;

import com.jdrbibli.auditservice.entity.AuditLog;
import com.jdrbibli.auditservice.service.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST permettant de gérer les journaux d'audit.
 * <p>
 * Les journaux d'audit enregistrent les actions effectuées
 * dans les différents microservices du projet JdrBibli.
 * Ce contrôleur fournit des endpoints pour :
 * <ul>
 *   <li>créer un nouveau log d'audit</li>
 *   <li>récupérer l'ensemble des logs existants</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/audit/logs")
public class AuditLogController {

    private final AuditLogService service;

    /**
     * Constructeur injectant le service métier lié aux logs d'audit.
     *
     * @param service service responsable de la gestion des {@link AuditLog}.
     */
    public AuditLogController(AuditLogService service) {
        this.service = service;
    }

    /**
     * Crée un nouveau log d'audit.
     *
     * @param log l'objet {@link AuditLog} à sauvegarder.
     * @return le log créé et persisté, encapsulé dans une {@link ResponseEntity}.
     */
    @PostMapping
    public ResponseEntity<AuditLog> createLog(@RequestBody AuditLog log) {
        return ResponseEntity.ok(service.save(log));
    }

    /**
     * Récupère l'ensemble des logs d'audit disponibles.
     *
     * @return la liste des {@link AuditLog}, encapsulée dans une {@link ResponseEntity}.
     */
    @GetMapping
    public ResponseEntity<List<AuditLog>> getAllLogs() {
        return ResponseEntity.ok(service.findAll());
    }
}

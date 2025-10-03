package com.jdrbibli.auditservice.service;

import com.jdrbibli.auditservice.entity.AuditLog;
import com.jdrbibli.auditservice.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service métier pour la gestion des logs d'audit.
 * <p>
 * Fournit des méthodes pour créer, sauvegarder et récupérer les {@link AuditLog}.
 * Ce service encapsule l'accès au {@link AuditLogRepository} et centralise la logique
 * métier liée aux logs d'audit.
 */
@Service
public class AuditLogService {

    private final AuditLogRepository repository;

    /**
     * Constructeur injectant le repository de logs d'audit.
     *
     * @param repository repository MongoDB pour les {@link AuditLog}.
     */
    public AuditLogService(AuditLogRepository repository) {
        this.repository = repository;
    }

    /**
     * Sauvegarde un log d'audit en base de données.
     *
     * @param log log à sauvegarder.
     * @return le {@link AuditLog} sauvegardé.
     */
    public AuditLog save(AuditLog log) {
        return repository.save(log);
    }

    /**
     * Récupère l'ensemble des logs d'audit stockés.
     *
     * @return liste de tous les {@link AuditLog}.
     */
    public List<AuditLog> findAll() {
        return repository.findAll();
    }
}

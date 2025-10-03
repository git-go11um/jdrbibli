package com.jdrbibli.auditservice.repository;

import com.jdrbibli.auditservice.entity.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository Spring Data MongoDB pour gérer les {@link AuditLog}.
 * 
 * Fournit les opérations CRUD standard pour les logs d'audit,
 * ainsi que les fonctionnalités de pagination et de tri si nécessaire.
 * 
 * 
 * Hérite de {@link MongoRepository} avec {@link AuditLog} comme type d'entité
 * et {@link String} comme type de l'identifiant.
 */
public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
}

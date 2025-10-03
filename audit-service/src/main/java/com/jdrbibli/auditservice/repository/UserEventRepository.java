package com.jdrbibli.auditservice.repository;

import com.jdrbibli.auditservice.entity.UserEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository Spring Data MongoDB pour gérer les {@link UserEvent}.
 * <p>
 * Fournit les opérations CRUD standard pour les événements utilisateur,
 * ainsi que les fonctionnalités de pagination et de tri si nécessaire.
 * 
 * <p>
 * Hérite de {@link MongoRepository} avec {@link UserEvent} comme type d'entité
 * et {@link String} comme type de l'identifiant.
 */
public interface UserEventRepository extends MongoRepository<UserEvent, String> {
}

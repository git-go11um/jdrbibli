package com.jdrbibli.ouvrage_service.repository;

import com.jdrbibli.ouvrage_service.entity.Gamme;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository pour l'entité {@link Gamme}.
 * 
 * Fournit des méthodes pour accéder et manipuler les gammes dans la base de données.
 * Hérite de {@link JpaRepository} pour bénéficier des opérations CRUD standards.
 */
public interface GammeRepository extends JpaRepository<Gamme, Long> {

    /**
     * Récupère toutes les gammes appartenant à un utilisateur spécifique.
     *
     * @param ownerId l'identifiant de l'utilisateur propriétaire des gammes
     * @return la liste des gammes appartenant à l'utilisateur
     */
    List<Gamme> findByOwnerId(Long ownerId);
}

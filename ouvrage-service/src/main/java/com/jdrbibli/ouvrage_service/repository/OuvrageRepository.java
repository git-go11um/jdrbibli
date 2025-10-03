package com.jdrbibli.ouvrage_service.repository;

import com.jdrbibli.ouvrage_service.entity.Ouvrage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité {@link Ouvrage}.
 * <p>
 * Fournit des méthodes pour accéder et manipuler les ouvrages dans la base de données.
 * Hérite de {@link JpaRepository} pour bénéficier des opérations CRUD standards.
 */
public interface OuvrageRepository extends JpaRepository<Ouvrage, Long> {

    /**
     * Compte le nombre d'ouvrages appartenant à une gamme spécifique.
     *
     * @param gammeId l'identifiant de la gamme
     * @return le nombre d'ouvrages de la gamme
     */
    long countByGammeId(Long gammeId);

    /**
     * Supprime tous les ouvrages d'une gamme spécifique.
     *
     * @param gammeId l'identifiant de la gamme
     */
    @Modifying
    @Transactional
    @Query("delete from Ouvrage o where o.gamme.id = :gammeId")
    void deleteByGammeId(Long gammeId);

    /**
     * Récupère tous les ouvrages appartenant à un utilisateur spécifique.
     *
     * @param ownerId l'identifiant du propriétaire
     * @return la liste des ouvrages de l'utilisateur
     */
    List<Ouvrage> findByOwnerId(Long ownerId);

    /**
     * Récupère tous les ouvrages d'une gamme spécifique appartenant à un utilisateur spécifique.
     *
     * @param gammeId l'identifiant de la gamme
     * @param ownerId l'identifiant du propriétaire
     * @return la liste des ouvrages correspondants
     */
    List<Ouvrage> findByGammeIdAndOwnerId(Long gammeId, Long ownerId);

    /**
     * Récupère tous les ouvrages d'une gamme spécifique, à l'exception d'un ouvrage donné.
     *
     * @param gammeId   l'identifiant de la gamme
     * @param excludeId l'identifiant de l'ouvrage à exclure
     * @return la liste des ouvrages correspondants
     */
    List<Ouvrage> findByGammeIdAndIdNot(Long gammeId, Long excludeId);

    /**
     * Récupère tous les ouvrages d'une gamme spécifique.
     *
     * @param gammeId l'identifiant de la gamme
     * @return la liste des ouvrages de la gamme
     */
    List<Ouvrage> findByGammeId(Long gammeId);

    /**
     * Récupère un ouvrage spécifique appartenant à un utilisateur spécifique.
     *
     * @param id      l'identifiant de l'ouvrage
     * @param ownerId l'identifiant du propriétaire
     * @return l'ouvrage correspondant, ou {@link Optional#empty()} si non trouvé
     */
    Optional<Ouvrage> findByIdAndOwnerId(Long id, Long ownerId);
}

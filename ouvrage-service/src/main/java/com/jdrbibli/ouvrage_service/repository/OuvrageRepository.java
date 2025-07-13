package com.jdrbibli.ouvrage_service.repository;

import com.jdrbibli.ouvrage_service.entity.Ouvrage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface OuvrageRepository extends JpaRepository<Ouvrage, Long> {
    long countByGammeId(Long gammeId);

    @Modifying
    @Transactional
    @Query("delete from Ouvrage o where o.gamme.id = :gammeId")
    void deleteByGammeId(Long gammeId);

    // ✅ Récupérer tous les ouvrages d’un utilisateur
    List<Ouvrage> findByOwnerPseudo(String ownerPseudo);

    // ✅ Récupérer tous les ouvrages d’une gamme ET appartenant à un owner
    List<Ouvrage> findByGammeIdAndOwnerPseudo(Long gammeId, String ownerPseudo);
}

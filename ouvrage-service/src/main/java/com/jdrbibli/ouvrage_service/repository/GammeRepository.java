package com.jdrbibli.ouvrage_service.repository;

import com.jdrbibli.ouvrage_service.entity.Gamme;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GammeRepository extends JpaRepository<Gamme, Long> {

    // ✅ Récupérer toutes les gammes d'un utilisateur
    List<Gamme> findByOwnerPseudo(String ownerPseudo);
}

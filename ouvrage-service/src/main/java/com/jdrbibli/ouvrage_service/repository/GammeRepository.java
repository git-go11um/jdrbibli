package com.jdrbibli.ouvrage_service.repository;

import com.jdrbibli.ouvrage_service.entity.Gamme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GammeRepository extends JpaRepository<Gamme, Long> {
    // Méthodes personnalisées si besoin
}

package com.jdrbibli.ouvrage_service.repository;

import com.jdrbibli.ouvrage_service.entity.Ouvrage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OuvrageRepository extends JpaRepository<Ouvrage, Long> {
    // Ici on peut ajouter plus tard des méthodes spécifiques, ex :
    // List<Ouvrage> findByLangue(String langue);
}

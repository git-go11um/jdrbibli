package com.jdrbibli.userservice.repository;

import com.jdrbibli.userservice.entity.Ouvrage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OuvrageRepository extends JpaRepository<Ouvrage, Long> {
    // Plus tard, on pourra ajouter des méthodes custom si besoin
}

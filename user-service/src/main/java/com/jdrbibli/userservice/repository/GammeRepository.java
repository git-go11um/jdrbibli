package com.jdrbibli.userservice.repository;

import com.jdrbibli.userservice.entity.Gamme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GammeRepository extends JpaRepository<Gamme, Long> {
    // Plus tard, on pourra ajouter des méthodes custom si besoin
}

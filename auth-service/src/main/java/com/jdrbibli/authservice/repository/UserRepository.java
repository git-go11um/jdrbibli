package com.jdrbibli.authservice.repository;

import com.jdrbibli.authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPseudo(String pseudo);
    boolean existsByEmail(String email);
    boolean existsByPseudo(String pseudo);
}

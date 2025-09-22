package com.jdrbibli.userservice.repository;

import com.jdrbibli.userservice.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByPseudo(String pseudo);

    Optional<UserProfile> findByEmail(String email);
}

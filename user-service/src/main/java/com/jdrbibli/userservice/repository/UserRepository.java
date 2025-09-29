package com.jdrbibli.userservice.repository;

import com.jdrbibli.userservice.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByPseudo(String pseudo);

    Optional<UserProfile> findByEmail(String email);

    @Query("SELECT u FROM UserProfile u " +
            "LEFT JOIN FETCH u.gammes g " +
            "LEFT JOIN FETCH g.ouvrages " +
            "WHERE u.id = :userId")
    Optional<UserProfile> findByIdWithGammesAndOuvrages(@Param("userId") Long userId);

}

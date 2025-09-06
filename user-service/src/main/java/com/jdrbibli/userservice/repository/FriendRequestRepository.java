package com.jdrbibli.userservice.repository;

import com.jdrbibli.userservice.entity.FriendRequest;
import com.jdrbibli.userservice.entity.FriendRequest.Status;
import com.jdrbibli.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

        List<FriendRequest> findBySender(User sender);

        List<FriendRequest> findByReceiver(User receiver);

        List<FriendRequest> findBySenderOrReceiver(User sender, User receiver);

        Optional<FriendRequest> findBySenderAndReceiver(User sender, User receiver);

        /**
         * Vérifie si une demande existe déjà entre deux utilisateurs,
         * dans un sens ou dans l'autre.
         */
        @Query("SELECT fr FROM FriendRequest fr WHERE " +
                        "(fr.sender = :user1 AND fr.receiver = :user2) OR " +
                        "(fr.sender = :user2 AND fr.receiver = :user1)")
        Optional<FriendRequest> findExistingRequestBetweenUsers(@Param("user1") User user1,
                        @Param("user2") User user2);

        /**
         * Vérifie si une amitié acceptée existe déjà entre deux utilisateurs.
         */
        @Query("SELECT fr FROM FriendRequest fr WHERE " +
                        "((fr.sender = :user1 AND fr.receiver = :user2) OR " +
                        "(fr.sender = :user2 AND fr.receiver = :user1)) AND fr.status = 'ACCEPTED'")
        Optional<FriendRequest> findAcceptedFriendshipBetweenUsers(@Param("user1") User user1,
                        @Param("user2") User user2);

        /**
         * Retourne toutes les amitiés acceptées d’un utilisateur.
         */
        @Query("SELECT fr FROM FriendRequest fr WHERE " +
                        "(fr.sender = :user OR fr.receiver = :user) AND fr.status = 'ACCEPTED'")
        List<FriendRequest> findAcceptedFriendshipsOfUser(@Param("user") User user);

        /**
         * Retourne toutes les demandes en attente reçues par un utilisateur.
         */
        List<FriendRequest> findByReceiverAndStatus(User receiver, Status status);
}

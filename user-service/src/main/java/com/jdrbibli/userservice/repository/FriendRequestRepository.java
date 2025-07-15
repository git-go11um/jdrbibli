package com.jdrbibli.userservice.repository;

import com.jdrbibli.userservice.entity.FriendRequest;
import com.jdrbibli.userservice.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

        List<FriendRequest> findBySender(UserProfile sender);

        List<FriendRequest> findByReceiver(UserProfile receiver);

        List<FriendRequest> findBySenderOrReceiver(UserProfile sender, UserProfile receiver);

        Optional<FriendRequest> findBySenderAndReceiver(UserProfile sender, UserProfile receiver);

        @Query("SELECT fr FROM FriendRequest fr WHERE " +
                        "(fr.sender = :user1 AND fr.receiver = :user2) OR " +
                        "(fr.sender = :user2 AND fr.receiver = :user1)")
        Optional<FriendRequest> findExistingRequestBetweenUsers(@Param("user1") UserProfile user1,
                        @Param("user2") UserProfile user2);

        @Query("SELECT fr FROM FriendRequest fr WHERE " +
                        "((fr.sender = :user1 AND fr.receiver = :user2) OR " +
                        "(fr.sender = :user2 AND fr.receiver = :user1)) AND fr.status = 'ACCEPTED'")
        Optional<FriendRequest> findAcceptedFriendshipBetweenUsers(@Param("user1") UserProfile user1,
                        @Param("user2") UserProfile user2);

        @Query("SELECT fr FROM FriendRequest fr WHERE " +
                        "(fr.sender = :user OR fr.receiver = :user) AND fr.status = 'ACCEPTED'")
        List<FriendRequest> findAcceptedFriendshipsOfUser(@Param("user") UserProfile user);

        List<FriendRequest> findByReceiverAndStatus(UserProfile receiver, FriendRequest.Status status);

        // Supprime les méthodes avec Long id + String status pour éviter confusion
}

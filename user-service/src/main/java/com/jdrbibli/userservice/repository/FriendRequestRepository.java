package com.jdrbibli.userservice.repository;

import com.jdrbibli.userservice.entity.FriendRequest;
import com.jdrbibli.userservice.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findBySender(UserProfile sender);
    List<FriendRequest> findByReceiver(UserProfile receiver);
    List<FriendRequest> findBySenderOrReceiver(UserProfile sender, UserProfile receiver);

    Optional<FriendRequest> findBySenderAndReceiver(UserProfile sender, UserProfile receiver);
}

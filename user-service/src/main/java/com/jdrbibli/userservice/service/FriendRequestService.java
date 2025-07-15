package com.jdrbibli.userservice.service;

import com.jdrbibli.userservice.entity.FriendRequest;
import com.jdrbibli.userservice.entity.FriendRequest.Status;
import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.repository.FriendRequestRepository;
import com.jdrbibli.userservice.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final UserProfileRepository userProfileRepository;

    @Autowired
    public FriendRequestService(FriendRequestRepository friendRequestRepository,
            UserProfileRepository userProfileRepository) {
        this.friendRequestRepository = friendRequestRepository;
        this.userProfileRepository = userProfileRepository;
    }

    public FriendRequest sendFriendRequest(Long senderId, Long receiverId) {
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Vous ne pouvez pas vous envoyer une demande à vous-même.");
        }

        UserProfile sender = userProfileRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Expéditeur non trouvé"));
        UserProfile receiver = userProfileRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Destinataire non trouvé"));

        Optional<FriendRequest> existing = friendRequestRepository
                .findExistingRequestBetweenUsers(sender, receiver);
        if (existing.isPresent()) {
            throw new RuntimeException("Une demande existe déjà entre ces utilisateurs.");
        }

        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus(Status.PENDING);

        return friendRequestRepository.save(request);
    }

    public FriendRequest acceptFriendRequest(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        request.setStatus(Status.ACCEPTED);
        return friendRequestRepository.save(request);
    }

    public FriendRequest rejectFriendRequest(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        request.setStatus(Status.REJECTED);
        return friendRequestRepository.save(request);
    }

    public void removeFriend(Long userId, Long friendId) {
        UserProfile user = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        UserProfile friend = userProfileRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Ami non trouvé"));

        Optional<FriendRequest> friendship = friendRequestRepository
                .findAcceptedFriendshipBetweenUsers(user, friend);

        if (friendship.isPresent()) {
            friendRequestRepository.delete(friendship.get());
        } else {
            throw new RuntimeException("Amitié non trouvée.");
        }
    }

    public List<UserProfile> listFriends(Long userId) {
        UserProfile user = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        List<FriendRequest> requests = friendRequestRepository.findAcceptedFriendshipsOfUser(user);

        return requests.stream()
                .map(r -> r.getSender().getId().equals(userId) ? r.getReceiver() : r.getSender())
                .collect(Collectors.toList());
    }

    public List<FriendRequest> listReceivedRequests(Long userId) {
        UserProfile user = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return friendRequestRepository.findByReceiverAndStatus(user, Status.PENDING);
    }

    // Suppression de la méthode qui utilisait Long + String
}

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

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    /**
     * Envoyer une demande d'ami.
     */
    public FriendRequest sendFriendRequest(Long senderId, Long receiverId) {
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Vous ne pouvez pas vous envoyer une demande à vous-même.");
        }

        UserProfile sender = userProfileRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Expéditeur non trouvé"));
        UserProfile receiver = userProfileRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Destinataire non trouvé"));

        // Vérifier s'il existe déjà une demande
        Optional<FriendRequest> existing = friendRequestRepository.findBySenderAndReceiver(sender, receiver);
        if (existing.isPresent()) {
            throw new RuntimeException("Une demande existe déjà entre ces utilisateurs.");
        }

        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus(Status.PENDING);

        return friendRequestRepository.save(request);
    }

    /**
     * Accepter une demande d'ami.
     */
    public FriendRequest acceptFriendRequest(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        request.setStatus(Status.ACCEPTED);
        return friendRequestRepository.save(request);
    }

    /**
     * Refuser une demande d'ami.
     */
    public FriendRequest rejectFriendRequest(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        request.setStatus(Status.REJECTED);
        return friendRequestRepository.save(request);
    }

    /**
     * Supprimer un ami (supprime la relation FriendRequest).
     */
    public void removeFriend(Long userId, Long friendId) {
        UserProfile user = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        UserProfile friend = userProfileRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Ami non trouvé"));

        // Trouver la demande acceptée dans les deux sens
        List<FriendRequest> requests = friendRequestRepository.findBySenderOrReceiver(user, user);
        requests.addAll(friendRequestRepository.findBySenderOrReceiver(friend, friend));

        for (FriendRequest fr : requests) {
            boolean match = (fr.getSender().getId().equals(userId) && fr.getReceiver().getId().equals(friendId))
                         || (fr.getSender().getId().equals(friendId) && fr.getReceiver().getId().equals(userId));
            if (match && fr.getStatus() == Status.ACCEPTED) {
                friendRequestRepository.delete(fr);
                return;
            }
        }

        throw new RuntimeException("Amitié non trouvée.");
    }

    /**
     * Lister mes amis (uniquement ceux acceptés).
     */
    public List<UserProfile> listFriends(Long userId) {
        UserProfile user = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        List<FriendRequest> requests = friendRequestRepository.findBySenderOrReceiver(user, user);

        return requests.stream()
                .filter(r -> r.getStatus() == Status.ACCEPTED)
                .map(r -> r.getSender().getId().equals(userId) ? r.getReceiver() : r.getSender())
                .collect(Collectors.toList());
    }
}

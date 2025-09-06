package com.jdrbibli.userservice.service;

import com.jdrbibli.userservice.entity.FriendRequest;
import com.jdrbibli.userservice.entity.FriendRequest.Status;
import com.jdrbibli.userservice.entity.User;
import com.jdrbibli.userservice.repository.FriendRequestRepository;
import com.jdrbibli.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;

    @Autowired
    public FriendRequestService(FriendRequestRepository friendRequestRepository,
                                UserRepository userRepository) {
        this.friendRequestRepository = friendRequestRepository;
        this.userRepository = userRepository;
    }

    

    /**
     * Envoie une demande d'ami.
     */
    public FriendRequest sendFriendRequest(Long senderId, Long receiverId) {
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Vous ne pouvez pas vous envoyer une demande à vous-même.");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Expéditeur non trouvé"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Destinataire non trouvé"));

        Optional<FriendRequest> existing = friendRequestRepository.findExistingRequestBetweenUsers(sender, receiver);
        if (existing.isPresent()) {
            throw new RuntimeException("Une demande existe déjà entre ces utilisateurs.");
        }

        FriendRequest request = new FriendRequest(sender, receiver);
        return friendRequestRepository.save(request);
    }

    /**
     * Accepte une demande d'ami.
     */
    public FriendRequest acceptFriendRequest(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        request.setStatus(Status.ACCEPTED);
        return friendRequestRepository.save(request);
    }

    /**
     * Rejette une demande d'ami.
     */
    public FriendRequest rejectFriendRequest(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        request.setStatus(Status.REJECTED);
        return friendRequestRepository.save(request);
    }

    /**
     * Supprime une amitié (dans les deux sens).
     */
    public void removeFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Ami non trouvé"));

        Optional<FriendRequest> friendship = friendRequestRepository.findAcceptedFriendshipBetweenUsers(user, friend);

        if (friendship.isPresent()) {
            friendRequestRepository.delete(friendship.get());
        } else {
            throw new RuntimeException("Amitié non trouvée.");
        }
    }

    /**
     * Retourne la liste des amis d'un utilisateur.
     */
    public List<User> listFriends(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        List<FriendRequest> requests = friendRequestRepository.findAcceptedFriendshipsOfUser(user);

        return requests.stream()
                .map(r -> r.getSender().getId().equals(userId) ? r.getReceiver() : r.getSender())
                .collect(Collectors.toList());
    }

    /**
     * Liste toutes les demandes d'amis reçues en attente.
     */
    public List<FriendRequest> listReceivedRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return friendRequestRepository.findByReceiverAndStatus(user, Status.PENDING);
    }
}

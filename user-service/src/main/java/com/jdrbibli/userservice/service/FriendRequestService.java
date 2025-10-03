package com.jdrbibli.userservice.service;

import com.jdrbibli.userservice.dto.GammeDTO;
import com.jdrbibli.userservice.dto.OuvrageDTO;
import com.jdrbibli.userservice.entity.FriendRequest;
import com.jdrbibli.userservice.entity.FriendRequest.Status;
import com.jdrbibli.userservice.entity.Gamme;
import com.jdrbibli.userservice.entity.Ouvrage;
import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.mapper.OuvrageMapper;
import com.jdrbibli.userservice.repository.FriendRequestRepository;
import com.jdrbibli.userservice.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service pour la gestion des demandes d'amis et des relations d'amitié entre utilisateurs.
 * 
 * Ce service permet d'envoyer, accepter, rejeter et supprimer des demandes d'amis,
 * ainsi que de lister les amis et leurs ouvrages.
 * 
 */
@Service
public class FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    private static final Logger log = LoggerFactory.getLogger(FriendRequestService.class);

    @Value("${ouvrage.service.url}")
    private String ouvrageServiceUrl;

    @Autowired
    public FriendRequestService(FriendRequestRepository friendRequestRepository,
            UserRepository userRepository,
            RestTemplate restTemplate) {
        this.friendRequestRepository = friendRequestRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    /**
     * Envoie une demande d'amitié d'un utilisateur vers un autre.
     *
     * @param senderId   ID de l'utilisateur envoyant la demande
     * @param receiverId ID de l'utilisateur recevant la demande
     * @return la demande d'ami créée
     * @throws IllegalArgumentException si l'utilisateur tente de s'envoyer une demande à lui-même
     * @throws RuntimeException         si l'un des utilisateurs n'existe pas ou si une demande existe déjà
     */
    public FriendRequest sendFriendRequest(Long senderId, Long receiverId) {
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Vous ne pouvez pas vous envoyer une demande à vous-même.");
        }

        UserProfile sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Expéditeur non trouvé"));
        UserProfile receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Destinataire non trouvé"));

        Optional<FriendRequest> existing = friendRequestRepository.findExistingRequestBetweenUsers(sender, receiver);
        if (existing.isPresent()) {
            throw new RuntimeException("Une demande existe déjà entre ces utilisateurs.");
        }

        FriendRequest request = new FriendRequest(sender, receiver);
        return friendRequestRepository.save(request);
    }

    /**
     * Accepte une demande d'amitié.
     *
     * @param requestId ID de la demande à accepter
     * @return la demande mise à jour
     * @throws RuntimeException si la demande n'existe pas
     */
    public FriendRequest acceptFriendRequest(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        request.setStatus(Status.ACCEPTED);
        return friendRequestRepository.save(request);
    }

    /**
     * Rejette une demande d'amitié.
     *
     * @param requestId ID de la demande à rejeter
     * @return la demande mise à jour
     * @throws RuntimeException si la demande n'existe pas
     */
    public FriendRequest rejectFriendRequest(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        request.setStatus(Status.REJECTED);
        return friendRequestRepository.save(request);
    }

    /**
     * Supprime une relation d'amitié existante entre deux utilisateurs.
     *
     * @param userId   ID de l'utilisateur
     * @param friendId ID de l'ami
     * @throws RuntimeException si l'amitié n'existe pas ou si l'un des utilisateurs n'existe pas
     */
    public void removeFriend(Long userId, Long friendId) {
        UserProfile user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        UserProfile friend = userRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Ami non trouvé"));

        Optional<FriendRequest> friendship = friendRequestRepository.findAcceptedFriendshipBetweenUsers(user, friend);

        if (friendship.isPresent()) {
            friendRequestRepository.delete(friendship.get());
        } else {
            throw new RuntimeException("Amitié non trouvée.");
        }
    }

    /**
     * Liste les amis d'un utilisateur.
     *
     * @param userId ID de l'utilisateur
     * @return liste des profils amis
     * @throws RuntimeException si l'utilisateur n'existe pas
     */
    public List<UserProfile> listFriends(Long userId) {
        UserProfile user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        List<FriendRequest> requests = friendRequestRepository.findAcceptedFriendshipsOfUser(user);

        return requests.stream()
                .map(r -> r.getSender().getId().equals(userId) ? r.getReceiver() : r.getSender())
                .collect(Collectors.toList());
    }

    /**
     * Liste les demandes d'amitié reçues en attente pour un utilisateur.
     *
     * @param userId ID de l'utilisateur
     * @return liste des demandes reçues
     */
    public List<FriendRequest> listReceivedRequests(Long userId) {
        List<FriendRequest> requests = friendRequestRepository.findByReceiverIdAndStatus(userId,
                FriendRequest.Status.PENDING);

        for (FriendRequest request : requests) {
            log.info("FriendRequest trouvé: id={}, sender={}, receiver={}, status={}",
                    request.getId(),
                    request.getSender() != null ? request.getSender().getId() : "null",
                    request.getReceiver() != null ? request.getReceiver().getId() : "null",
                    request.getStatus());
        }

        return requests;
    }

    /**
     * Vérifie si deux utilisateurs sont amis.
     *
     * @param userId1 ID du premier utilisateur
     * @param userId2 ID du second utilisateur
     * @return true si les deux utilisateurs sont amis
     * @throws RuntimeException si l'un des utilisateurs n'existe pas
     */
    public boolean areFriends(Long userId1, Long userId2) {
        UserProfile user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        UserProfile user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return friendRequestRepository.findAcceptedFriendshipBetweenUsers(user1, user2).isPresent();
    }

    /**
     * Récupère la liste des ouvrages publics d'un ami via le service Ouvrage.
     *
     * @param friendId ID de l'ami
     * @return liste des ouvrages de l'ami
     * @throws RuntimeException si l'utilisateur n'existe pas
     */
    public List<OuvrageDTO> listFriendOuvrages(Long friendId) {
        UserProfile friend = userRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        log.info("Ami trouvé: id={}, pseudo={}", friend.getId(), friend.getPseudo());

        String url = String.format("%s/gammes/public/owner/%d", ouvrageServiceUrl, friendId);

        GammeDTO[] gammes = restTemplate.getForObject(url, GammeDTO[].class);

        if (gammes == null)
            return List.of();

        List<OuvrageDTO> ouvrages = new ArrayList<>();
        for (GammeDTO g : gammes) {
            if (g.getOuvrages() != null) {
                ouvrages.addAll(g.getOuvrages());
            }
        }

        log.info("#ouvrages récupérés={}", ouvrages.size());
        return ouvrages;
    }
}

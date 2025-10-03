package com.jdrbibli.userservice.controller;

import com.jdrbibli.userservice.dto.FriendDTO;
import com.jdrbibli.userservice.dto.FriendRequestDTO;
import com.jdrbibli.userservice.dto.OuvrageDTO;
import com.jdrbibli.userservice.entity.FriendRequest;
import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.mapper.FriendMapper;
import com.jdrbibli.userservice.mapper.FriendRequestMapper;
import com.jdrbibli.userservice.service.FriendRequestService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur REST pour gérer les demandes d'amitié et la liste d'amis.
 * 
 * Fournit des endpoints pour envoyer, accepter, rejeter des demandes d'amitié,
 * supprimer des amis, lister les amis et accéder aux ouvrages d'un ami.
 */
@RestController
@RequestMapping("/api/friends")
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    /**
     * Constructeur du contrôleur.
     *
     * @param friendRequestService service gérant la logique métier des amis
     */
    public FriendRequestController(FriendRequestService friendRequestService) {
        this.friendRequestService = friendRequestService;
    }

    /**
     * Envoie une demande d'amitié d'un utilisateur vers un autre.
     *
     * @param senderId   ID de l'utilisateur qui envoie la demande
     * @param receiverId ID de l'utilisateur qui reçoit la demande
     * @return DTO représentant la demande d'amitié créée
     */
    @PostMapping("/request")
    public FriendRequestDTO sendRequest(@RequestParam Long senderId, @RequestParam Long receiverId) {
        FriendRequest request = friendRequestService.sendFriendRequest(senderId, receiverId);
        return FriendRequestMapper.toDTO(request);
    }

    /**
     * Accepte une demande d'amitié.
     *
     * @param requestId ID de la demande à accepter
     * @return DTO de la demande d'amitié acceptée
     */
    @PostMapping("/{requestId}/accept")
    public FriendRequestDTO acceptRequest(@PathVariable Long requestId) {
        FriendRequest request = friendRequestService.acceptFriendRequest(requestId);
        return FriendRequestMapper.toDTO(request);
    }

    /**
     * Rejette une demande d'amitié.
     *
     * @param requestId ID de la demande à rejeter
     * @return DTO de la demande d'amitié rejetée
     */
    @PostMapping("/{requestId}/reject")
    public FriendRequestDTO rejectRequest(@PathVariable Long requestId) {
        FriendRequest request = friendRequestService.rejectFriendRequest(requestId);
        return FriendRequestMapper.toDTO(request);
    }

    /**
     * Supprime un ami de la liste d'un utilisateur.
     *
     * @param userId   ID de l'utilisateur qui supprime l'ami
     * @param friendId ID de l'ami à supprimer
     */
    @DeleteMapping("/{friendId}")
    public void removeFriend(@RequestParam Long userId, @PathVariable Long friendId) {
        friendRequestService.removeFriend(userId, friendId);
    }

    /**
     * Liste tous les amis d'un utilisateur.
     *
     * @param userId ID de l'utilisateur
     * @return liste de DTO représentant les amis
     */
    @GetMapping
    public List<FriendDTO> listFriends(@RequestParam Long userId) {
        List<UserProfile> friends = friendRequestService.listFriends(userId);
        return friends.stream()
                .map(FriendMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Liste les demandes d'amitié reçues par un utilisateur.
     *
     * @param userId ID de l'utilisateur
     * @return liste de DTO représentant les demandes reçues
     */
    @GetMapping("/requests/received")
    public List<FriendRequestDTO> listReceivedRequests(@RequestParam Long userId) {
        List<FriendRequest> requests = friendRequestService.listReceivedRequests(userId);
        return requests.stream()
                .map(FriendRequestMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les ouvrages d'un ami si la relation d'amitié existe.
     *
     * @param friendId ID de l'ami dont on veut les ouvrages
     * @param userId   ID de l'utilisateur qui effectue la requête (header X-User-Id)
     * @return liste de DTO représentant les ouvrages de l'ami
     * @throws ResponseStatusException si les deux utilisateurs ne sont pas amis
     */
    @GetMapping("/{friendId}/ouvrages")
    public List<OuvrageDTO> getFriendOuvrages(
            @PathVariable Long friendId,
            @RequestHeader("X-User-Id") Long userId) {

        boolean areFriends = friendRequestService.areFriends(userId, friendId);
        if (!areFriends) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pas ami avec cet utilisateur");
        }

        return friendRequestService.listFriendOuvrages(friendId);
    }

}

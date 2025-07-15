package com.jdrbibli.userservice.controller;

import com.jdrbibli.userservice.dto.FriendDTO;
import com.jdrbibli.userservice.dto.FriendRequestDTO;
import com.jdrbibli.userservice.entity.FriendRequest;
import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.mapper.FriendMapper;
import com.jdrbibli.userservice.mapper.FriendRequestMapper;
import com.jdrbibli.userservice.service.FriendRequestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/friends")
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    public FriendRequestController(FriendRequestService friendRequestService) {
        this.friendRequestService = friendRequestService;
    }

    /**
     * Envoyer une demande d'ami.
     */
    @PostMapping("/request")
    public FriendRequestDTO sendRequest(@RequestParam Long senderId, @RequestParam Long receiverId) {
        FriendRequest request = friendRequestService.sendFriendRequest(senderId, receiverId);
        return FriendRequestMapper.toDTO(request);
    }

    /**
     * Accepter une demande d'ami.
     */
    @PostMapping("/{requestId}/accept")
    public FriendRequestDTO acceptRequest(@PathVariable Long requestId) {
        FriendRequest request = friendRequestService.acceptFriendRequest(requestId);
        return FriendRequestMapper.toDTO(request);
    }

    /**
     * Refuser une demande d'ami.
     */
    @PostMapping("/{requestId}/reject")
    public FriendRequestDTO rejectRequest(@PathVariable Long requestId) {
        FriendRequest request = friendRequestService.rejectFriendRequest(requestId);
        return FriendRequestMapper.toDTO(request);
    }

    /**
     * Supprimer un ami existant.
     */
    @DeleteMapping("/{friendId}")
    public void removeFriend(@RequestParam Long userId, @PathVariable Long friendId) {
        friendRequestService.removeFriend(userId, friendId);
    }

    /**
     * Lister les amis d’un utilisateur.
     */
    @GetMapping
    public List<FriendDTO> listFriends(@RequestParam Long userId) {
        List<UserProfile> friends = friendRequestService.listFriends(userId);
        return friends.stream()
                .map(FriendMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/requests/received")
    public List<FriendRequestDTO> listReceivedRequests(@RequestParam Long userId) {
        List<FriendRequest> requests = friendRequestService.listReceivedRequests(userId);
        return requests.stream()
                .map(FriendRequestMapper::toDTO)
                .collect(Collectors.toList());
    }

}

package com.jdrbibli.userservice.service;

import com.jdrbibli.userservice.dto.GammeDTO;
import com.jdrbibli.userservice.dto.OuvrageDTO;
import com.jdrbibli.userservice.entity.FriendRequest;
import com.jdrbibli.userservice.entity.FriendRequest.Status;
import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.repository.FriendRequestRepository;
import com.jdrbibli.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FriendRequestServiceTest {

    private FriendRequestRepository friendRequestRepository;
    private UserRepository userRepository;
    private RestTemplate restTemplate;
    private FriendRequestService service;

    @BeforeEach
    void setUp() {
        friendRequestRepository = mock(FriendRequestRepository.class);
        userRepository = mock(UserRepository.class);
        restTemplate = mock(RestTemplate.class);
        service = new FriendRequestService(friendRequestRepository, userRepository, restTemplate);
        service.setOuvrageServiceUrl("http://localhost:8083/api/ouvrage");

    }

    @Test
    void sendFriendRequest_shouldCreateRequest() {
        UserProfile sender = new UserProfile(); sender.setId(1L);
        UserProfile receiver = new UserProfile(); receiver.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(friendRequestRepository.findExistingRequestBetweenUsers(sender, receiver)).thenReturn(Optional.empty());
        when(friendRequestRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        FriendRequest request = service.sendFriendRequest(1L, 2L);

        assertEquals(sender, request.getSender());
        assertEquals(receiver, request.getReceiver());
        assertEquals(Status.PENDING, request.getStatus());
    }

    @Test
    void acceptFriendRequest_shouldSetStatusAccepted() {
        FriendRequest request = new FriendRequest();
        request.setStatus(Status.PENDING);
        when(friendRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(friendRequestRepository.save(request)).thenReturn(request);

        FriendRequest result = service.acceptFriendRequest(1L);

        assertEquals(Status.ACCEPTED, result.getStatus());
    }

    @Test
    void rejectFriendRequest_shouldSetStatusRejected() {
        FriendRequest request = new FriendRequest();
        request.setStatus(Status.PENDING);
        when(friendRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(friendRequestRepository.save(request)).thenReturn(request);

        FriendRequest result = service.rejectFriendRequest(1L);

        assertEquals(Status.REJECTED, result.getStatus());
    }

    @Test
    void listFriends_shouldReturnCorrectFriends() {
        UserProfile user = new UserProfile(); user.setId(1L);
        UserProfile friend1 = new UserProfile(); friend1.setId(2L);
        UserProfile friend2 = new UserProfile(); friend2.setId(3L);

        FriendRequest fr1 = new FriendRequest(); fr1.setSender(user); fr1.setReceiver(friend1); fr1.setStatus(Status.ACCEPTED);
        FriendRequest fr2 = new FriendRequest(); fr2.setSender(friend2); fr2.setReceiver(user); fr2.setStatus(Status.ACCEPTED);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(friendRequestRepository.findAcceptedFriendshipsOfUser(user)).thenReturn(List.of(fr1, fr2));

        List<UserProfile> friends = service.listFriends(1L);
        assertEquals(2, friends.size());
        assertTrue(friends.contains(friend1));
        assertTrue(friends.contains(friend2));
    }

    @Test
    void areFriends_shouldReturnTrueIfAccepted() {
        UserProfile user1 = new UserProfile(); user1.setId(1L);
        UserProfile user2 = new UserProfile(); user2.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(friendRequestRepository.findAcceptedFriendshipBetweenUsers(user1, user2)).thenReturn(Optional.of(new FriendRequest()));

        assertTrue(service.areFriends(1L, 2L));
    }

    @Test
    void listFriendOuvrages_shouldReturnOuvrages() {
        UserProfile friend = new UserProfile(); friend.setId(2L); friend.setPseudo("toto");
        when(userRepository.findById(2L)).thenReturn(Optional.of(friend));

        OuvrageDTO ouvrage = new OuvrageDTO();
        GammeDTO gamme = new GammeDTO();
        gamme.setOuvrages(List.of(ouvrage));

        when(restTemplate.getForObject("http://localhost:8083/api/ouvrage/gammes/public/owner/2", GammeDTO[].class))
                .thenReturn(new GammeDTO[]{gamme});

        List<OuvrageDTO> result = service.listFriendOuvrages(2L);

        assertEquals(1, result.size());
        assertEquals(ouvrage, result.get(0));
    }
}

package com.jdrbibli.userservice.service;

import com.jdrbibli.userservice.entity.FriendRequest;
import com.jdrbibli.userservice.entity.FriendRequest.Status;
import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.repository.FriendRequestRepository;
import com.jdrbibli.userservice.repository.UserProfileRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FriendRequestServiceTest {

    @InjectMocks
    private FriendRequestService friendRequestService;

    @Mock
    private FriendRequestRepository friendRequestRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    private UserProfile sender;
    private UserProfile receiver;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        sender = new UserProfile();
        sender.setId(1L);
        sender.setPseudo("sender");

        receiver = new UserProfile();
        receiver.setId(2L);
        receiver.setPseudo("receiver");
    }

    @Test
    void sendFriendRequest_success() {
        when(userProfileRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
        when(userProfileRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));
        when(friendRequestRepository.findBySenderAndReceiver(sender, receiver)).thenReturn(Optional.empty());

        FriendRequest savedRequest = new FriendRequest();
        savedRequest.setId(100L);
        savedRequest.setSender(sender);
        savedRequest.setReceiver(receiver);
        savedRequest.setStatus(Status.PENDING);

        when(friendRequestRepository.save(any(FriendRequest.class))).thenReturn(savedRequest);

        FriendRequest result = friendRequestService.sendFriendRequest(sender.getId(), receiver.getId());

        assertNotNull(result);
        assertEquals(Status.PENDING, result.getStatus());
        assertEquals(sender, result.getSender());
        assertEquals(receiver, result.getReceiver());

        verify(friendRequestRepository).save(any(FriendRequest.class));
    }

    @Test
    void sendFriendRequest_sameSenderReceiver_throws() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> friendRequestService.sendFriendRequest(1L, 1L));
        assertEquals("Vous ne pouvez pas vous envoyer une demande à vous-même.", ex.getMessage());
    }

    @Test
    void sendFriendRequest_existingRequest_throws() {
        when(userProfileRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
        when(userProfileRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));
        when(friendRequestRepository.findBySenderAndReceiver(sender, receiver))
                .thenReturn(Optional.of(new FriendRequest()));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> friendRequestService.sendFriendRequest(sender.getId(), receiver.getId()));
        assertEquals("Une demande existe déjà entre ces utilisateurs.", ex.getMessage());
    }

    @Test
    void acceptFriendRequest_success() {
        FriendRequest request = new FriendRequest();
        request.setId(10L);
        request.setStatus(Status.PENDING);

        when(friendRequestRepository.findById(10L)).thenReturn(Optional.of(request));
        when(friendRequestRepository.save(request)).thenReturn(request);

        FriendRequest result = friendRequestService.acceptFriendRequest(10L);

        assertEquals(Status.ACCEPTED, result.getStatus());
        verify(friendRequestRepository).save(request);
    }

    @Test
    void acceptFriendRequest_notFound_throws() {
        when(friendRequestRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> friendRequestService.acceptFriendRequest(99L));
        assertEquals("Demande non trouvée", ex.getMessage());
    }

    @Test
    void rejectFriendRequest_success() {
        FriendRequest request = new FriendRequest();
        request.setId(20L);
        request.setStatus(Status.PENDING);

        when(friendRequestRepository.findById(20L)).thenReturn(Optional.of(request));
        when(friendRequestRepository.save(request)).thenReturn(request);

        FriendRequest result = friendRequestService.rejectFriendRequest(20L);

        assertEquals(Status.REJECTED, result.getStatus());
        verify(friendRequestRepository).save(request);
    }

    @Test
    void rejectFriendRequest_notFound_throws() {
        when(friendRequestRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> friendRequestService.rejectFriendRequest(999L));
        assertEquals("Demande non trouvée", ex.getMessage());
    }

    @Test
    void removeFriend_success() {
        UserProfile user = new UserProfile();
        user.setId(1L);
        UserProfile friend = new UserProfile();
        friend.setId(2L);

        FriendRequest fr = new FriendRequest();
        fr.setSender(user);
        fr.setReceiver(friend);
        fr.setStatus(Status.ACCEPTED);

        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userProfileRepository.findById(2L)).thenReturn(Optional.of(friend));
        when(friendRequestRepository.findBySenderOrReceiver(user, user)).thenReturn(new ArrayList<>(List.of(fr)));
        when(friendRequestRepository.findBySenderOrReceiver(friend, friend)).thenReturn(new ArrayList<>());

        friendRequestService.removeFriend(1L, 2L);

        verify(friendRequestRepository).delete(fr);
    }

    @Test
    void removeFriend_notFound_throws() {
        UserProfile user = new UserProfile();
        user.setId(1L);
        UserProfile friend = new UserProfile();
        friend.setId(2L);

        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userProfileRepository.findById(2L)).thenReturn(Optional.of(friend));
        when(friendRequestRepository.findBySenderOrReceiver(user, user)).thenReturn(new ArrayList<>());
        when(friendRequestRepository.findBySenderOrReceiver(friend, friend)).thenReturn(new ArrayList<>());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> friendRequestService.removeFriend(1L, 2L));
        assertEquals("Amitié non trouvée.", ex.getMessage());
    }

    @Test
    void listFriends_success() {
        UserProfile user = new UserProfile();
        user.setId(1L);

        UserProfile friend1 = new UserProfile();
        friend1.setId(2L);

        UserProfile friend2 = new UserProfile();
        friend2.setId(3L);

        FriendRequest fr1 = new FriendRequest();
        fr1.setSender(user);
        fr1.setReceiver(friend1);
        fr1.setStatus(Status.ACCEPTED);

        FriendRequest fr2 = new FriendRequest();
        fr2.setSender(friend2);
        fr2.setReceiver(user);
        fr2.setStatus(Status.ACCEPTED);

        FriendRequest fr3 = new FriendRequest();
        fr3.setSender(user);
        fr3.setReceiver(friend1);
        fr3.setStatus(Status.PENDING);

        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(user));
        when(friendRequestRepository.findBySenderOrReceiver(user, user)).thenReturn(List.of(fr1, fr2, fr3));

        List<UserProfile> friends = friendRequestService.listFriends(1L);

        assertEquals(2, friends.size());
        assertTrue(friends.contains(friend1));
        assertTrue(friends.contains(friend2));
    }

    @Test
    void listFriends_userNotFound_throws() {
        when(userProfileRepository.findById(123L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> friendRequestService.listFriends(123L));
        assertEquals("Utilisateur non trouvé", ex.getMessage());
    }
}

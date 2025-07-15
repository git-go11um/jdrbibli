package com.jdrbibli.userservice.controller;

import com.jdrbibli.userservice.dto.FriendDTO;
import com.jdrbibli.userservice.dto.FriendRequestDTO;
import com.jdrbibli.userservice.entity.FriendRequest;
import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.mapper.FriendRequestMapper;
import com.jdrbibli.userservice.service.FriendRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FriendRequestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FriendRequestService friendRequestService;

    @InjectMocks
    private FriendRequestController friendRequestController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(friendRequestController).build();
    }

    @Test
    public void testSendRequest() throws Exception {
        // Crée les UserProfiles pour sender et receiver
        UserProfile sender = new UserProfile();
        sender.setId(1L);
        sender.setPseudo("senderPseudo");

        UserProfile receiver = new UserProfile();
        receiver.setId(2L);
        receiver.setPseudo("receiverPseudo");

        // Crée la FriendRequest avec sender, receiver et status
        FriendRequest fakeRequest = new FriendRequest();
        fakeRequest.setId(1L);
        fakeRequest.setSender(sender);
        fakeRequest.setReceiver(receiver);
        fakeRequest.setStatus(FriendRequest.Status.PENDING);

        when(friendRequestService.sendFriendRequest(1L, 2L)).thenReturn(fakeRequest);

        mockMvc.perform(post("/friends/request")
                .param("senderId", "1")
                .param("receiverId", "2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.senderId", is(1)))
                .andExpect(jsonPath("$.receiverId", is(2)));

        verify(friendRequestService).sendFriendRequest(1L, 2L);
    }

    @Test
    public void testAcceptRequest() throws Exception {
        UserProfile sender = new UserProfile();
        sender.setId(1L);
        sender.setPseudo("senderPseudo");

        UserProfile receiver = new UserProfile();
        receiver.setId(2L);
        receiver.setPseudo("receiverPseudo");

        FriendRequest acceptedRequest = new FriendRequest();
        acceptedRequest.setId(10L);
        acceptedRequest.setSender(sender);
        acceptedRequest.setReceiver(receiver);
        acceptedRequest.setStatus(FriendRequest.Status.ACCEPTED);

        when(friendRequestService.acceptFriendRequest(10L)).thenReturn(acceptedRequest);

        mockMvc.perform(post("/friends/10/accept"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.status", is("ACCEPTED")))
                .andExpect(jsonPath("$.senderId", is(1)))
                .andExpect(jsonPath("$.receiverId", is(2)));

        verify(friendRequestService).acceptFriendRequest(10L);
    }

    @Test
    public void testRejectRequest() throws Exception {
        UserProfile sender = new UserProfile();
        sender.setId(1L);
        sender.setPseudo("senderPseudo");

        UserProfile receiver = new UserProfile();
        receiver.setId(2L);
        receiver.setPseudo("receiverPseudo");

        FriendRequest rejectedRequest = new FriendRequest();
        rejectedRequest.setId(10L);
        rejectedRequest.setSender(sender);
        rejectedRequest.setReceiver(receiver);
        rejectedRequest.setStatus(FriendRequest.Status.REJECTED);

        when(friendRequestService.rejectFriendRequest(10L)).thenReturn(rejectedRequest);

        mockMvc.perform(post("/friends/10/reject"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.status", is("REJECTED")))
                .andExpect(jsonPath("$.senderId", is(1)))
                .andExpect(jsonPath("$.receiverId", is(2)));

        verify(friendRequestService).rejectFriendRequest(10L);
    }

    @Test
    public void testRemoveFriend() throws Exception {
        doNothing().when(friendRequestService).removeFriend(1L, 2L);

        mockMvc.perform(delete("/friends/2")
                .param("userId", "1"))
                .andExpect(status().isOk());

        verify(friendRequestService).removeFriend(1L, 2L);
    }

    @Test
    public void testListFriends() throws Exception {
        UserProfile user1 = new UserProfile();
        user1.setId(2L);
        user1.setPseudo("friend1");
        user1.setEmail("friend1@example.com");

        List<UserProfile> friends = List.of(user1);

        when(friendRequestService.listFriends(1L)).thenReturn(friends);

        mockMvc.perform(get("/friends")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].pseudo", is("friend1")))
                .andExpect(jsonPath("$[0].email", is("friend1@example.com")));

        verify(friendRequestService).listFriends(1L);
    }
}

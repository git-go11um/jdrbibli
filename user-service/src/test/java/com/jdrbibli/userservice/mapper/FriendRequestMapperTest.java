package com.jdrbibli.userservice.mapper;

import com.jdrbibli.userservice.dto.FriendDTO;
import com.jdrbibli.userservice.dto.FriendRequestDTO;
import com.jdrbibli.userservice.entity.FriendRequest;
import com.jdrbibli.userservice.entity.UserProfile;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FriendRequestMapperTest {

    @Test
    void toDTO_shouldMapAllFields() {
        UserProfile sender = new UserProfile();
        sender.setId(1L);
        sender.setPseudo("Alice");

        UserProfile receiver = new UserProfile();
        receiver.setId(2L);
        receiver.setPseudo("Bob");

        FriendRequest request = new FriendRequest(sender, receiver, FriendRequest.Status.PENDING);
        request.setId(100L);
        request.setRespondedAt(LocalDateTime.now());
        request.setCreatedAt(LocalDateTime.now());

        FriendRequestDTO dto = FriendRequestMapper.toDTO(request);

        assertNotNull(dto);
        assertEquals(request.getId(), dto.getId());
        assertEquals("PENDING", dto.getStatus());
        assertEquals(request.getCreatedAt(), dto.getCreatedAt());
        assertEquals(request.getRespondedAt(), dto.getRespondedAt());

        assertEquals(sender.getId(), dto.getSenderId());
        assertEquals(sender.getPseudo(), dto.getSenderPseudo());

        assertEquals(receiver.getId(), dto.getReceiverId());
        assertEquals(receiver.getPseudo(), dto.getReceiverPseudo());
    }

    @Test
    void toDTO_shouldReturnNullIfRequestIsNull() {
        assertNull(FriendRequestMapper.toDTO((FriendRequest) null));
    }

    @Test
    void toDTO_shouldHandleNullUser() {
        FriendRequest request = new FriendRequest(null, null);
        request.setId(101L);
        request.setStatus(FriendRequest.Status.ACCEPTED);

        FriendRequestDTO dto = FriendRequestMapper.toDTO(request);

        assertNotNull(dto);
        assertEquals(101L, dto.getId());
        assertEquals("ACCEPTED", dto.getStatus());
        assertNull(dto.getSenderId());
        assertNull(dto.getSenderPseudo());
        assertNull(dto.getReceiverId());
        assertNull(dto.getReceiverPseudo());
    }

    @Test
    void toDTO_UserProfile_shouldMapCorrectly() {
        UserProfile user = new UserProfile();
        user.setId(5L);
        user.setPseudo("Charlie");
        user.setEmail("charlie@example.com");
        user.setAvatarUrl("http://avatar.url/charlie.png");

        FriendDTO dto = FriendRequestMapper.toDTO(user);

        assertNotNull(dto);
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getPseudo(), dto.getPseudo());
        assertEquals(user.getEmail(), dto.getEmail());
        assertEquals(user.getAvatarUrl(), dto.getAvatarUrl());
    }

    @Test
    void toDTO_UserProfile_shouldReturnNullIfUserIsNull() {
        assertNull(FriendRequestMapper.toDTO((UserProfile) null));
    }
}

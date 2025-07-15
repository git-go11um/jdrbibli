package com.jdrbibli.userservice.mapper;

import com.jdrbibli.userservice.dto.FriendDTO;
import com.jdrbibli.userservice.dto.FriendRequestDTO;
import com.jdrbibli.userservice.entity.FriendRequest;
import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.entity.FriendRequest.Status;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FriendRequestMapperTest {

    @Test
    public void testToDTO_FriendRequest() {
        UserProfile sender = new UserProfile();
        sender.setId(1L);
        sender.setPseudo("senderPseudo");

        UserProfile receiver = new UserProfile();
        receiver.setId(2L);
        receiver.setPseudo("receiverPseudo");

        FriendRequest request = new FriendRequest();
        request.setId(10L);
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus(Status.PENDING);

        FriendRequestDTO dto = FriendRequestMapper.toDTO(request);

        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals(1L, dto.getSenderId());
        assertEquals("senderPseudo", dto.getSenderPseudo());
        assertEquals(2L, dto.getReceiverId());
        assertEquals("receiverPseudo", dto.getReceiverPseudo());
        assertEquals("PENDING", dto.getStatus());
    }

    @Test
    public void testToDTO_FriendDTO() {
        UserProfile user = new UserProfile();
        user.setId(5L);
        user.setPseudo("userPseudo");
        user.setEmail("user@example.com");
        user.setAvatarUrl("http://avatar.url/image.png");

        FriendDTO dto = FriendRequestMapper.toDTO(user);

        assertNotNull(dto);
        assertEquals(5L, dto.getId());
        assertEquals("userPseudo", dto.getPseudo());
        assertEquals("user@example.com", dto.getEmail());
        assertEquals("http://avatar.url/image.png", dto.getAvatarUrl());
    }
}

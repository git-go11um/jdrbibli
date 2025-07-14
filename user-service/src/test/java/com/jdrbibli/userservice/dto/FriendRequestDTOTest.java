package com.jdrbibli.userservice.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class FriendRequestDTOTest {

    @Test
    void testGettersAndSetters() {
        FriendRequestDTO dto = new FriendRequestDTO();

        Long id = 1L;
        Long senderId = 2L;
        String senderPseudo = "senderUser";
        Long receiverId = 3L;
        String receiverPseudo = "receiverUser";
        String status = "PENDING";

        dto.setId(id);
        dto.setSenderId(senderId);
        dto.setSenderPseudo(senderPseudo);
        dto.setReceiverId(receiverId);
        dto.setReceiverPseudo(receiverPseudo);
        dto.setStatus(status);

        assertEquals(id, dto.getId());
        assertEquals(senderId, dto.getSenderId());
        assertEquals(senderPseudo, dto.getSenderPseudo());
        assertEquals(receiverId, dto.getReceiverId());
        assertEquals(receiverPseudo, dto.getReceiverPseudo());
        assertEquals(status, dto.getStatus());
    }
}
